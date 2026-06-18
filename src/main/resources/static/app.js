const form          = document.getElementById('shorten-form');
const urlInput      = document.getElementById('url-input');
const shortenBtn    = document.getElementById('shorten-btn');
const errorMsg      = document.getElementById('error-msg');
const resultSection = document.getElementById('result');
const shortUrlLink  = document.getElementById('short-url');
const copyBtn       = document.getElementById('copy-btn');
const aliasInput    = document.getElementById('alias-input');
const aliasGroup    = document.getElementById('alias-group');
const qrDiv         = document.getElementById('qr-code');

// Limpia errores mientras el usuario escribe
urlInput.addEventListener('input', () => {
    urlInput.classList.remove('input--error');
    errorMsg.hidden = true;
});

aliasInput.addEventListener('input', () => {
    aliasGroup.classList.remove('alias--error');
    errorMsg.hidden = true;
});

form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const url   = urlInput.value.trim();
    const alias = aliasInput.value.trim();

    if (!url) {
        showError('Pega una URL antes de acortar.');
        return;
    }

    setLoading(true);
    resultSection.hidden = true;
    qrDiv.innerHTML = '';
    errorMsg.hidden = true;

    const body = { url };
    if (alias) body.alias = alias;

    try {
        const response = await fetch('/api/links', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });

        const data = await response.json();

        if (!response.ok) {
            const message = data.fieldErrors?.[0] ?? data.message ?? 'Ha ocurrido un error inesperado.';
            if (response.status === 409 || (alias && data.message?.includes('alias'))) {
                aliasGroup.classList.add('alias--error');
            } else {
                urlInput.classList.add('input--error');
            }
            showError(message);
            return;
        }

        aliasInput.value = '';
        showResult(data.shortUrl);

    } catch {
        showError('No se pudo conectar con el servidor. Comprueba tu conexión.');
    } finally {
        setLoading(false);
    }
});

copyBtn.addEventListener('click', async () => {
    const url = shortUrlLink.href;

    try {
        await navigator.clipboard.writeText(url);
    } catch {
        // Fallback para navegadores sin Clipboard API
        const tmp = document.createElement('textarea');
        tmp.value = url;
        tmp.style.position = 'fixed';
        tmp.style.opacity = '0';
        document.body.appendChild(tmp);
        tmp.select();
        document.execCommand('copy');
        document.body.removeChild(tmp);
    }

    copyBtn.classList.add('btn--copied');
    copyBtn.querySelector('.btn__text').textContent = '¡Copiado!';

    setTimeout(() => {
        copyBtn.classList.remove('btn--copied');
        copyBtn.querySelector('.btn__text').textContent = 'Copiar';
    }, 2000);
});

function setLoading(on) {
    shortenBtn.disabled = on;
    shortenBtn.classList.toggle('btn--loading', on);
}

function showError(message) {
    errorMsg.textContent = message;
    errorMsg.hidden = false;
}

function showResult(shortUrl) {
    shortUrlLink.href = shortUrl;
    shortUrlLink.textContent = shortUrl;

    qrDiv.innerHTML = '';
    new QRCode(qrDiv, {
        text: shortUrl,
        width: 160,
        height: 160,
        colorDark: '#1e293b',
        colorLight: '#ffffff',
        correctLevel: QRCode.CorrectLevel.H
    });

    setTimeout(() => {
        const canvas = qrDiv.querySelector('canvas');
        if (!canvas) return;
        canvas.addEventListener('click', () => {
            const link = document.createElement('a');
            link.download = 'qr-code.png';
            link.href = canvas.toDataURL('image/png');
            link.click();
        });
    }, 0);

    resultSection.hidden = false;
}
