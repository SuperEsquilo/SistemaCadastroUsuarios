const API_BASE_URL = "http://localhost:8080";
const formLogin = document.getElementById("form-login");
const emailInput = document.getElementById("email-login");
const senhaInput = document.getElementById("senha-login");
const formCadastro = document.getElementById("form-cadastro");
const nomeCadastro = document.getElementById("nome-cadastro");
const emailCadastro = document.getElementById("email-cadastro");
const telefoneCadastro = document.getElementById("telefone-cadastro");
const cpfCadastro = document.getElementById("cpf-cadastro");
const rgCadastro = document.getElementById("rg-cadastro");
const senhaCadastro = document.getElementById("senha-cadastro");
const confirmarSenha = document.getElementById("confirmar-senha");
const tagUsuario = document.getElementById("tag-usuario");
const botaoCriarConta = formCadastro.querySelector(".btn-entrar");
const botaoMostrarCadastro = document.getElementById("mostrar-cadastro");
const botaoVoltarLogin = document.getElementById("voltar-login");
const canvas = document.getElementById("fundo-animado");
const contexto = canvas.getContext("2d");
const movimentoReduzido = window.matchMedia(
    "(prefers-reduced-motion: reduce)"
).matches;

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

let loginEmAndamento = false;

async function validarLogin(event) {
    event.preventDefault();

    if (loginEmAndamento) return;

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

    loginEmAndamento = true;
    const botaoLogin = formLogin.querySelector(".btn-entrar");
    if (botaoLogin) botaoLogin.disabled = true;

    try {
        const resposta = await fetch(`${API_BASE_URL}/usuarios/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Accept: "application/json"
            },
            body: JSON.stringify({ email, senha })
        });

        if (resposta.status === 404) {
            showToast(
                "O endpoint de login ainda não foi implementado no backend.",
                "error"
            );
            return;
        }

        const corpo = await resposta.text();
        let resultado = null;
        if (corpo) {
            try { resultado = JSON.parse(corpo); } catch { resultado = null; }
        }

        if (!resposta.ok) {
            const mensagem =
                resultado?.detail ||
                resultado?.message ||
                resultado?.error ||
                (resposta.status === 401
                    ? "E-mail ou senha incorretos."
                    : `Erro HTTP ${resposta.status} ao tentar autenticar.`);
            showToast(mensagem, "error");
            return;
        }

        showToast("Login realizado com sucesso! Redirecionando...", "success");

        // Salva os dados do usuário logado e redireciona para o dashboard
        if (resultado) {
            sessionStorage.setItem("usuarioLogado", JSON.stringify(resultado));
        }
        setTimeout(() => {
            window.location.href = "dashboard.html";
        }, 1200);
    } catch (erro) {
        const mensagem =
            erro instanceof TypeError
                ? "Não foi possível conectar ao backend. Verifique se ele está ativo e se permite requisições CORS desta página."
                : erro.message;
        showToast(mensagem, "error");
    } finally {
        loginEmAndamento = false;
        if (botaoLogin) botaoLogin.disabled = false;
    }
}

formLogin.addEventListener("submit", validarLogin);
formLogin.querySelector(".btn-entrar").addEventListener("click", validarLogin);

// Máscaras amigáveis para campos de cadastro
if (telefoneCadastro) {
    telefoneCadastro.addEventListener("input", (e) => {
        let v = e.target.value.replace(/\D/g, "").slice(0, 11);
        if (v.length > 10) {
            v = v.replace(/^(\d{2})(\d{5})(\d{4})$/, "($1) $2-$3");
        } else if (v.length > 6) {
            v = v.replace(/^(\d{2})(\d{4})(\d{0,4})$/, "($1) $2-$3");
        } else if (v.length > 2) {
            v = v.replace(/^(\d{2})(\d{0,5})$/, "($1) $2");
        }
        e.target.value = v;
    });
}

if (cpfCadastro) {
    cpfCadastro.addEventListener("input", (e) => {
        let v = e.target.value.replace(/\D/g, "").slice(0, 11);
        if (v.length > 9) {
            v = v.replace(/^(\d{3})(\d{3})(\d{3})(\d{1,2})$/, "$1.$2.$3-$4");
        } else if (v.length > 6) {
            v = v.replace(/^(\d{3})(\d{3})(\d{0,3})$/, "$1.$2.$3");
        } else if (v.length > 3) {
            v = v.replace(/^(\d{3})(\d{0,3})$/, "$1.$2");
        }
        e.target.value = v;
    });
}

if (rgCadastro) {
    rgCadastro.addEventListener("input", (e) => {
        let v = e.target.value.replace(/\D/g, "").slice(0, 9);
        if (v.length > 8) {
            v = v.replace(/^(\d{2})(\d{3})(\d{3})(\d{1})$/, "$1.$2.$3-$4");
        } else if (v.length > 5) {
            v = v.replace(/^(\d{2})(\d{3})(\d{0,3})$/, "$1.$2.$3");
        } else if (v.length > 2) {
            v = v.replace(/^(\d{2})(\d{0,3})$/, "$1.$2");
        }
        e.target.value = v;
    });
}

async function enviarCadastro(dados) {
    const resposta = await fetch(`${API_BASE_URL}/usuarios`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Accept: "application/json"
        },
        body: JSON.stringify(dados)
    });

    const corpo = await resposta.text();
    let resultado = null;

    if (corpo) {
        try {
            resultado = JSON.parse(corpo);
        } catch {
            resultado = null;
        }
    }

    if (!resposta.ok) {
        const mensagem =
            resultado?.detail ||
            resultado?.message ||
            resultado?.error ||
            (resposta.status === 400
                ? "Os dados enviados foram rejeitados. Verifique o CPF, e-mail e demais campos."
                : `O backend respondeu com erro HTTP ${resposta.status}.`);
        throw new Error(mensagem);
    }

    return resultado;
}

let cadastroEmAndamento = false;

async function validarCadastro(event) {
    event.preventDefault();

    if (cadastroEmAndamento) {
        return;
    }

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

    const telefoneLimpo = telefoneCadastro ? telefoneCadastro.value.replace(/\D/g, "") : "";
    if (!telefoneLimpo) {
        showToast("Preencha seu telefone.", "error");
        telefoneCadastro?.focus();
        return;
    }

    const cpfLimpo = cpfCadastro ? cpfCadastro.value.replace(/\D/g, "") : "";
    if (!cpfLimpo) {
        showToast("Preencha seu CPF.", "error");
        cpfCadastro?.focus();
        return;
    }

    if (cpfLimpo.length !== 11) {
        showToast("O CPF deve conter 11 dígitos.", "error");
        cpfCadastro?.focus();
        return;
    }

    const rgLimpo = rgCadastro ? rgCadastro.value.replace(/\D/g, "") : "";
    if (!rgLimpo) {
        showToast("Preencha seu RG.", "error");
        rgCadastro?.focus();
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

    const tiposUsuario = {
        usuario: "USUARIO",
        admin: "ADMINISTRADOR"
    };
    const tipoUsuario = tiposUsuario[tagUsuario.value];

    if (!tipoUsuario) {
        showToast(
            "O backend não oferece suporte ao tipo de usuário selecionado.",
            "error"
        );
        tagUsuario.focus();
        return;
    }

    const dados = {
        nome: nomeCadastro.value.trim(),
        email,
        telefone: telefoneLimpo,
        cpf: cpfLimpo,
        rg: rgLimpo,
        senha: senhaCadastro.value,
        tipoUsuario
    };

    cadastroEmAndamento = true;
    botaoCriarConta.disabled = true;

    try {
        await enviarCadastro(dados);
        showToast("Cadastro criado com sucesso! Agora você pode fazer login.", "success");
        formCadastro.reset();
        trocarTela(formCadastro, formLogin);
    } catch (erro) {
        const mensagem =
            erro instanceof TypeError
                ? "Não foi possível conectar ao backend. Verifique se ele está ativo e se permite requisições CORS desta página."
                : erro.message;
        showToast(mensagem, "error");
    } finally {
        cadastroEmAndamento = false;
        botaoCriarConta.disabled = false;
    }
}

formCadastro.addEventListener("submit", validarCadastro);
botaoCriarConta.addEventListener("click", validarCadastro);

botaoMostrarCadastro.addEventListener("click", () => {
    trocarTela(formLogin, formCadastro);
});

botaoVoltarLogin.addEventListener("click", () => {
    trocarTela(formCadastro, formLogin);
});

let largura;
let altura;
let pontos = [];

function ajustarCanvas() {
    const escala = window.devicePixelRatio || 1;

    largura = window.innerWidth;
    altura = window.innerHeight;

    canvas.width = largura * escala;
    canvas.height = altura * escala;
    contexto.setTransform(escala, 0, 0, escala, 0, 0);

    const quantidade = Math.min(100, Math.floor((largura * altura) / 12000));

    pontos = Array.from({ length: quantidade }, () => ({
        x: Math.random() * largura,
        y: Math.random() * altura,
        raio: Math.random() * 2 + 1,
        velocidadeX: (Math.random() - 0.5) * 0.4,
        velocidadeY: (Math.random() - 0.5) * 0.4
    }));

    desenhar();
}

function desenhar() {
    contexto.clearRect(0, 0, largura, altura);

    const distanciaMaxima = 120;

    // Desenha linhas entre pontos próximos
    for (let i = 0; i < pontos.length; i++) {
        for (let j = i + 1; j < pontos.length; j++) {
            const dx = pontos[i].x - pontos[j].x;
            const dy = pontos[i].y - pontos[j].y;
            const distancia = Math.hypot(dx, dy);

            if (distancia < distanciaMaxima) {
                contexto.beginPath();
                contexto.moveTo(pontos[i].x, pontos[i].y);
                contexto.lineTo(pontos[j].x, pontos[j].y);
                contexto.strokeStyle = "rgb(231 231 231 / 0.39)";
                contexto.globalAlpha = 1 - distancia / distanciaMaxima;
                contexto.lineWidth = 1;
                contexto.stroke();
            }
        }
    }

    contexto.globalAlpha = 1;
    contexto.fillStyle = "rgb(255 255 255 / 0.88)";

    for (const ponto of pontos) {
        contexto.beginPath();
        contexto.arc(ponto.x, ponto.y, ponto.raio, 0, Math.PI * 2);
        contexto.fill();

        ponto.x += ponto.velocidadeX;
        ponto.y += ponto.velocidadeY;

        if (ponto.x < 0 || ponto.x > largura) ponto.velocidadeX *= -1;
        if (ponto.y < 0 || ponto.y > altura) ponto.velocidadeY *= -1;
    }

    if (!movimentoReduzido) {
        requestAnimationFrame(desenhar);
    }
}

window.addEventListener("resize", ajustarCanvas);
ajustarCanvas();