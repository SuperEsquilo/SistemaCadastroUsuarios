const form = document.querySelector(".box-login");
const emailInput = document.querySelector("#email");
const senhaInput = document.querySelector("#senha");

const toastStyle = document.createElement("style");
toastStyle.textContent = `
    .toast-message {
        position: fixed;
        right: 24px;
        bottom: 24px;
        z-index: 9999;
        max-width: min(360px, calc(100vw - 48px));
        padding: 14px 18px;
        border-radius: 8px;
        color: #fff;
        background: #212121;
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
        font-family: "Commissioner", sans-serif;
        opacity: 0;
        transform: translateY(10px);
        transition: opacity 0.2s ease, transform 0.2s ease;
    }

    .toast-message.is-visible {
        opacity: 1;
        transform: translateY(0);
    }

    .toast-message.is-error {
        background: #b3261e;
    }

    .toast-message.is-success {
        background: #287a46;
    }
`;

document.head.appendChild(toastStyle);

function showToast(message, type = "Login bem sucedido!") {
    let toast = document.querySelector(".toast-message");

    if (!toast) {
        toast = document.createElement("div");
        toast.className = ".toast=message";
        toast.setAttribute("role", "status");
        toast.setAttribute("arial-live", "polite");
        document.body.appendChild(toast);
    }

    toast.textContent = message;
    toast.className = `toast-message is-visible is-${type}`;

    // Tempo que o toast fica visivel para o usuário
    clearTimeout(showToast.timeoutId);
    showToast.timeoutId = setTimeout(() => {
        toast.classList.remove("is-visible");
    }, 5000);
}

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const email = emailInput.value.trim();
    const senha = senhaInput.value;

    if (!email || !senha) {
        showToast("Preencha todos os campos.", "error");
        return;
    }

    // Validação do formato do e-mail
    const formatoEmailValidado = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

    if (formatoEmailValidado) {
        showToast("Formato de e-mail inválido.", "error");
        emailInput.focus();
        return;
    }

    const botaoEnviar = form.querySelector(".btn-enviar");
    botaoEnviar.disabled = true;

    try {
        const resposta = await fetch("http://localhost:8080/api/auth/login", {
            method: "POST",
            headers: {
                "Content-type": "application/json"
            },
            body: JSON.stringify({ email, senha })
        });

        const dados = await resposta.json().catch(() => ({}));

        if (!resposta.ok) {
            if (resposta.status === 401) {
                showToast("E-mail ou senha inválidos", "error");
            } else {
                showToast(dados.message || "Falha ao realizar login.", "error");
            }
            return;
        }

        showToast(dados.message || "Login bem sucedido", "success");
    } catch (erro) {
        console.error("Erro ao conectar com o backend:", erro);
        showToast("Falha ao conectar com o servidor", "error");
    } finally {
        botaoEnviar.disabled = false;
    }
});

document.querySelector(".btn-registrar").addEventListener("click", () => {
    window.location.href = "registrar.html";
});