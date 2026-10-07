const formLogin = document.getElementById("form-login");
const emailInput = document.getElementById("email-login");
const senhaInput = document.getElementById("senha-login");
const formCadastro = document.getElementById("form-cadastro");
const nomeCadastro = document.getElementById("nome-cadastro");
const emailCadastro = document.getElementById("email-cadastro");
const senhaCadastro = document.getElementById("senha-cadastro");
const confirmarSenha = document.getElementById("confirmar-senha");
const tagUsuario = document.getElementById("tag-usuario");
const botaoCriarConta = formCadastro.querySelector(".btn-entrar");
const botaoMostrarCadastro = document.getElementById("mostrar-cadastro");
const botaoVoltarLogin = document.getElementById("voltar-login");

const toastStyle = document.createElement("style");
toastStyle.textContent = `
    .toast-message {
        position: fixed;
        right: 24px;
        bottom: 24px;
        z-index: 9999;
        max-width: min(360px, calc(100vw - 48px));
        padding: 14px 18px 14px 22px;
        border-radius: 8px;
        overflow: hidden;
        color: #fff;
        background: #212121;
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
        font-family: "Commissioner", sans-serif;
        opacity: 0;
        transform: translateY(10px);
        transition: opacity 0.2s ease, transform 0.2s ease;
        --toast-accent: #9e9e9e;
    }

    .toast-message::before {
        content: "";
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        width: 10px;
        background: var(--toast-accent);
    }

    .toast-message.is-visible {
        opacity: 1;
        transform: translateY(0);
    }

    .toast-message.is-error {
        --toast-accent: #b3261e;
    }

    .toast-message.is-success {
        --toast-accent: #287a46;
    }
`;
document.head.appendChild(toastStyle);

const formTransitionStyle = document.createElement("style");
formTransitionStyle.textContent = `
    .box-login {
        opacity: 1;
        transform: translateY(0);
        transition: opacity 220ms ease, transform 220ms ease;
    }

    .box-login.is-leaving {
        opacity: 0;
        transform: translateY(-8px);
        pointer-events: none;
    }

    .box-login.is-entering {
        opacity: 0;
        transform: translateY(8px);
    }

    @media (prefers-reduced-motion: reduce) {
        .box-login {
            transition-duration: 0.01ms;
        }
    }
`;
document.head.appendChild(formTransitionStyle);

let toastTimeoutId;
let trocandoTela = false;

function showToast(message, type = "info") {
    let toast = document.querySelector(".toast-message");

    if (!toast) {
        toast = document.createElement("div");
        toast.className = "toast-message";
        toast.setAttribute("role", "status");
        toast.setAttribute("aria-live", "polite");
        document.body.appendChild(toast);
    }

    clearTimeout(toastTimeoutId);
    toast.textContent = message;
    toast.className = `toast-message is-${type}`;

    requestAnimationFrame(() => {
        toast.classList.add("is-visible");
    });

    toastTimeoutId = setTimeout(() => {
        toast.classList.remove("is-visible");
    }, 5000);
}

function aguardarTransicao(elemento, iniciarTransicao) {
    return new Promise((resolve) => {
        let finalizada = false;

        const finalizar = (event) => {
            if (
                event &&
                (event.target !== elemento || event.propertyName !== "opacity")
            ) {
                return;
            }

            if (finalizada) {
                return;
            }

            finalizada = true;
            clearTimeout(timeoutId);
            elemento.removeEventListener("transitionend", finalizar);
            resolve();
        };

        const timeoutId = setTimeout(finalizar, 300);
        elemento.addEventListener("transitionend", finalizar);
        iniciarTransicao();
    });
}

async function trocarTela(telaAtual, proximaTela) {
    if (trocandoTela) {
        return;
    }

    trocandoTela = true;

    try {
        await aguardarTransicao(telaAtual, () => {
            telaAtual.classList.add("is-leaving");
        });

        telaAtual.hidden = true;
        telaAtual.setAttribute("aria-hidden", "true");
        telaAtual.classList.remove("is-leaving");

        proximaTela.hidden = false;
        proximaTela.setAttribute("aria-hidden", "false");
        proximaTela.classList.add("is-entering");

        void proximaTela.offsetWidth;

        await aguardarTransicao(proximaTela, () => {
            requestAnimationFrame(() => {
                proximaTela.classList.remove("is-entering");
            });
        });

        proximaTela.querySelector("input")?.focus();
    } finally {
        trocandoTela = false;
    }
}

function validarLogin(event) {
    event.preventDefault();

    const email = emailInput.value.trim();
    const senha = senhaInput.value;

    if (!email || !senha) {
        showToast("Preencha todos os campos.", "error");
        return;
    }

    const formatoEmailValido = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

    if (!formatoEmailValido) {
        showToast("Formato de e-mail inválido.", "error");
        emailInput.focus();
        return;
    }

    showToast("Login ainda não conectado ao backend.", "info");
}

formLogin.addEventListener("submit", validarLogin);
formLogin.querySelector(".btn-entrar").addEventListener("click", validarLogin);

function validarCadastro(event) {
    event.preventDefault();

    if (!nomeCadastro.value.trim()) {
        showToast("Preencha seu nome.", "error");
        nomeCadastro.focus();
        return;
    }

    const email = emailCadastro.value.trim();
    const formatoEmailValido = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

    if (!email) {
        showToast("Preencha o e-mail.", "error");
        emailCadastro.focus();
        return;
    }

    if (!formatoEmailValido) {
        showToast("Formato de e-mail inválido.", "error");
        emailCadastro.focus();
        return;
    }

    if (!senhaCadastro.value) {
        showToast("Preencha a senha.", "error");
        senhaCadastro.focus();
        return;
    }

    if (!confirmarSenha.value) {
        showToast("Confirme sua senha.", "error");
        confirmarSenha.focus();
        return;
    }

    if (senhaCadastro.value !== confirmarSenha.value) {
        showToast("As senhas não coincidem.", "error");
        confirmarSenha.focus();
        return;
    }

    if (!tagUsuario.value) {
        showToast("Selecione o tipo de usuário.", "error");
        tagUsuario.focus();
        return;
    }

    showToast("Campos validados. O cadastro ainda não está conectado ao backend.", "info");
}

botaoCriarConta.addEventListener("click", validarCadastro);

botaoMostrarCadastro.addEventListener("click", () => {
    trocarTela(formLogin, formCadastro);
});

botaoVoltarLogin.addEventListener("click", () => {
    trocarTela(formCadastro, formLogin);
});
