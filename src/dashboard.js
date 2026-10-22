/* ════════════════════════════════════════════════════════════
   dashboard.js  — Sky Client
   Lógica de negócio + UI do painel de gestão
════════════════════════════════════════════════════════════ */

const API_BASE_URL = "http://localhost:8080";

/* ──────────────────────────────────────────────────────────
   1. GUARDA DE ROTA
────────────────────────────────────────────────────────── */
const usuarioRaw = sessionStorage.getItem("usuarioLogado");
if (!usuarioRaw) {
    window.location.replace("index.html");
    throw new Error("Não autenticado.");
}

const usuarioLogado = JSON.parse(usuarioRaw);
const isAdmin = (usuarioLogado.tipoUsuario || "").toUpperCase() === "ADMINISTRADOR";

/* ──────────────────────────────────────────────────────────
   2. REFERÊNCIAS DOM
────────────────────────────────────────────────────────── */
const elNome           = document.getElementById("dash-nome-usuario");
const elTag            = document.getElementById("dash-tag-usuario");
const elLogout         = document.getElementById("btn-logout");
const elWelcomeNome    = document.getElementById("welcome-nome");
const elWelcomeDesc    = document.getElementById("welcome-desc");
const btnMenuMobile    = document.getElementById("btn-menu-mobile");
const mobileMenu       = document.getElementById("mobile-menu"); // compatibilidade

// Métricas
const elTotal          = document.getElementById("total-usuarios");
const elAtivos         = document.getElementById("total-ativos");
const elInativos       = document.getElementById("total-inativos");
const elAdmins         = document.getElementById("total-admins");

// Tabela principal
const elTbody          = document.getElementById("tabela-usuarios-body");
const elLoading        = document.getElementById("dash-loading");
const elErro           = document.getElementById("dash-erro");
const elBusca          = document.getElementById("busca-usuario");

// Tabela resumida (visão geral)
const elTbodyRecentes  = document.getElementById("tabela-recentes-body");

// Paginação
const elPagInfo        = document.getElementById("pag-info");
const elPagPages       = document.getElementById("pag-pages");
const btnPagAnterior   = document.getElementById("btn-pag-anterior");
const btnPagProximo    = document.getElementById("btn-pag-proximo");

// Modal usuário
const modalUsuario     = document.getElementById("modal-usuario");
const modalTitulo      = document.getElementById("modal-titulo");
const formModal        = document.getElementById("form-modal");
const modalId          = document.getElementById("modal-id");
const mNome            = document.getElementById("m-nome");
const mEmail           = document.getElementById("m-email");
const mTelefone        = document.getElementById("m-telefone");
const mCpf             = document.getElementById("m-cpf");
const mRg              = document.getElementById("m-rg");
const mSenha           = document.getElementById("m-senha");
const mTipo            = document.getElementById("m-tipo");
const btnModalFechar   = document.getElementById("modal-fechar");
const btnModalCancelar = document.getElementById("modal-cancelar");

// Modal confirmação
const modalConfirmar   = document.getElementById("modal-confirmar");
const confirmMensagem  = document.getElementById("confirm-mensagem");
const confirmOk        = document.getElementById("confirm-ok");
const confirmCancelar  = document.getElementById("confirm-cancelar");

// Toast
const elToast          = document.getElementById("toast-dash");

// Painel admin
const inputIdAtivar    = document.getElementById("input-id-ativar");
const inputIdDesativar = document.getElementById("input-id-desativar");
const inputIdTipo      = document.getElementById("input-id-tipo");
const selectTipo       = document.getElementById("select-tipo");
const inputIdDeletar   = document.getElementById("input-id-deletar");

// Sidebar avatar
const sidebarAvatar    = document.getElementById("sidebar-avatar-initials");

/* ──────────────────────────────────────────────────────────
   3. ESTADO
────────────────────────────────────────────────────────── */
let listaUsuarios   = [];
let listaFiltrada   = [];
let toastTimer      = null;
let confirmCallback = null;
let sortField       = "id";
let sortAsc         = true;
let paginaAtual     = 1;
const POR_PAGINA    = 10;

// Expõe para os scripts inline do HTML
window._listaUsuarios = listaUsuarios;

/* ──────────────────────────────────────────────────────────
   4. TOAST
────────────────────────────────────────────────────────── */
function showToast(mensagem, tipo = "info") {
    clearTimeout(toastTimer);
    elToast.textContent = mensagem;
    elToast.className = `toast is-${tipo} is-visible`;
    toastTimer = setTimeout(() => elToast.classList.remove("is-visible"), 4500);
}

/* ──────────────────────────────────────────────────────────
   5. MODAL DE CONFIRMAÇÃO
────────────────────────────────────────────────────────── */
function confirmar(mensagem, callback, classBotao = "btn-danger") {
    confirmMensagem.textContent = mensagem;
    confirmOk.className = `btn-action ${classBotao}`;
    confirmCallback = callback;
    abrirModal(modalConfirmar);
}

confirmOk.addEventListener("click", () => {
    fecharModal(modalConfirmar);
    if (confirmCallback) { confirmCallback(); confirmCallback = null; }
});

confirmCancelar.addEventListener("click", () => fecharModal(modalConfirmar));

/* ──────────────────────────────────────────────────────────
   6. HELPERS DE MODAL
────────────────────────────────────────────────────────── */
function abrirModal(modal) {
    modal.hidden = false;
    document.body.style.overflow = "hidden";
    const focusable = modal.querySelector("button, input, select");
    if (focusable) setTimeout(() => focusable.focus(), 50);
}

function fecharModal(modal) {
    modal.hidden = true;
    document.body.style.overflow = "";
}

[modalUsuario, modalConfirmar].forEach(modal => {
    modal.addEventListener("click", e => { if (e.target === modal) fecharModal(modal); });
});

document.addEventListener("keydown", e => {
    if (e.key === "Escape") {
        if (!modalUsuario.hidden)   fecharModal(modalUsuario);
        if (!modalConfirmar.hidden) fecharModal(modalConfirmar);
    }
});

/* ──────────────────────────────────────────────────────────
   7. CABEÇALHO / PERFIL
────────────────────────────────────────────────────────── */
const primeiroNome = (usuarioLogado.nome || "Usuário").split(" ")[0];
elNome.textContent       = usuarioLogado.nome || "Usuário";
elWelcomeNome.textContent = primeiroNome;

if (sidebarAvatar) {
    sidebarAvatar.textContent = (usuarioLogado.nome || "U")[0].toUpperCase();
}

if (isAdmin) {
    elTag.textContent = "Admin";
    elTag.classList.add("admin");
    elWelcomeDesc.textContent = "Você tem acesso completo de administrador.";
    document.querySelectorAll(".admin-only").forEach(el => {
        el.classList.remove("hidden");
        el.hidden = false;
    });
} else {
    elTag.textContent = "Usuário";
    elWelcomeDesc.textContent = "Visualize os dados dos clientes cadastrados.";
}

/* ──────────────────────────────────────────────────────────
   8. LOGOUT
────────────────────────────────────────────────────────── */
elLogout.addEventListener("click", () => {
    sessionStorage.removeItem("usuarioLogado");
    window.location.href = "index.html";
});

/* ──────────────────────────────────────────────────────────
   9. SIDEBAR MOBILE
────────────────────────────────────────────────────────── */
const sidebar        = document.getElementById("sidebar");
const sidebarOverlay = document.getElementById("sidebar-overlay");

btnMenuMobile?.addEventListener("click", () => {
    const aberto = sidebar.classList.toggle("sidebar--open");
    sidebarOverlay.hidden = !aberto;
    btnMenuMobile.setAttribute("aria-expanded", String(aberto));
    // compatibilidade dashboard.js legado
    if (mobileMenu) mobileMenu.hidden = true;
});

/* ──────────────────────────────────────────────────────────
   10. NAVEGAÇÃO ENTRE SEÇÕES
────────────────────────────────────────────────────────── */
function ativarSecao(nomeSecao) {
    document.querySelectorAll(".sidebar-link").forEach(el => {
        const ativo = el.dataset.section === nomeSecao;
        el.classList.toggle("active", ativo);
        el.setAttribute("aria-current", ativo ? "page" : "false");
    });

    document.querySelectorAll(".dash-section").forEach(sec => {
        const ativa = sec.id === `section-${nomeSecao}`;
        sec.hidden = !ativa;
        sec.classList.toggle("active", ativa);
    });

    // Fecha sidebar mobile
    sidebar.classList.remove("sidebar--open");
    if (sidebarOverlay) sidebarOverlay.hidden = true;
    btnMenuMobile?.setAttribute("aria-expanded", "false");

    // Atualiza breadcrumb
    const pageTitles = { "visao-geral": "Visão Geral", "usuarios": "Clientes", "administracao": "Administração" };
    const titleEl = document.getElementById("topbar-page-title");
    if (titleEl) titleEl.textContent = pageTitles[nomeSecao] || nomeSecao;
}

// Links do sidebar
document.querySelectorAll(".sidebar-link").forEach(link => {
    link.addEventListener("click", e => {
        e.preventDefault();
        ativarSecao(link.dataset.section);
    });
});

// Link "Ver todos" na visão geral
document.querySelectorAll(".link-ver-todos").forEach(link => {
    link.addEventListener("click", e => {
        e.preventDefault();
        ativarSecao(link.dataset.section || "usuarios");
    });
});

// Inicia na visão geral
ativarSecao("visao-geral");

/* ──────────────────────────────────────────────────────────
   11. MÁSCARAS
────────────────────────────────────────────────────────── */
mTelefone?.addEventListener("input", e => {
    let v = e.target.value.replace(/\D/g, "").slice(0, 11);
    if (v.length > 10) v = v.replace(/^(\d{2})(\d{5})(\d{4})$/, "($1) $2-$3");
    else if (v.length > 6) v = v.replace(/^(\d{2})(\d{4})(\d{0,4})$/, "($1) $2-$3");
    else if (v.length > 2) v = v.replace(/^(\d{2})(\d{0,5})$/, "($1) $2");
    e.target.value = v;
});

mCpf?.addEventListener("input", e => {
    let v = e.target.value.replace(/\D/g, "").slice(0, 11);
    if (v.length > 9) v = v.replace(/^(\d{3})(\d{3})(\d{3})(\d{1,2})$/, "$1.$2.$3-$4");
    else if (v.length > 6) v = v.replace(/^(\d{3})(\d{3})(\d{0,3})$/, "$1.$2.$3");
    else if (v.length > 3) v = v.replace(/^(\d{3})(\d{0,3})$/, "$1.$2");
    e.target.value = v;
});

mRg?.addEventListener("input", e => {
    let v = e.target.value.replace(/\D/g, "").slice(0, 9);
    if (v.length > 8) v = v.replace(/^(\d{2})(\d{3})(\d{3})(\d{1})$/, "$1.$2.$3-$4");
    else if (v.length > 5) v = v.replace(/^(\d{2})(\d{3})(\d{0,3})$/, "$1.$2.$3");
    else if (v.length > 2) v = v.replace(/^(\d{2})(\d{0,3})$/, "$1.$2");
    e.target.value = v;
});

/* ──────────────────────────────────────────────────────────
   12. MODAL DE NOVO USUÁRIO
────────────────────────────────────────────────────────── */
function abrirModalNovo() {
    formModal.reset();
    modalId.value = "";
    modalTitulo.textContent = "Novo Cliente";
    document.getElementById("modal-salvar").textContent = "Criar Conta";
    mSenha.required = true;
    abrirModal(modalUsuario);
}

document.getElementById("btn-novo-usuario")?.addEventListener("click", abrirModalNovo);
document.getElementById("btn-novo-usuario-2")?.addEventListener("click", abrirModalNovo);
btnModalFechar.addEventListener("click", () => fecharModal(modalUsuario));
btnModalCancelar.addEventListener("click", () => fecharModal(modalUsuario));

/* ──────────────────────────────────────────────────────────
   13. SALVAR USUÁRIO
────────────────────────────────────────────────────────── */
formModal.addEventListener("submit", async e => {
    e.preventDefault();

    const nome  = mNome.value.trim();
    const email = mEmail.value.trim();
    const tel   = mTelefone.value.replace(/\D/g, "");
    const cpf   = mCpf.value.replace(/\D/g, "");
    const rg    = mRg.value.replace(/\D/g, "");
    const senha = mSenha.value;
    const tipo  = mTipo.value;

    if (!nome)               { showToast("Preencha o nome.", "error");               mNome.focus();     return; }
    if (!email)              { showToast("Preencha o e-mail.", "error");             mEmail.focus();    return; }
    if (!tel)                { showToast("Preencha o telefone.", "error");           mTelefone.focus(); return; }
    if (!cpf || cpf.length !== 11) { showToast("CPF inválido (11 dígitos).", "error"); mCpf.focus();   return; }
    if (!rg)                 { showToast("Preencha o RG.", "error");                 mRg.focus();       return; }
    if (!senha)              { showToast("Preencha a senha.", "error");              mSenha.focus();    return; }
    if (!tipo)               { showToast("Selecione o tipo de usuário.", "error");   mTipo.focus();     return; }

    const btn = document.getElementById("modal-salvar");
    btn.disabled = true;
    btn.textContent = "Salvando…";

    try {
        const resp = await fetch(`${API_BASE_URL}/usuarios`, {
            method: "POST",
            headers: { "Content-Type": "application/json", Accept: "application/json" },
            body: JSON.stringify({ nome, email, telefone: tel, cpf, rg, senha, tipoUsuario: tipo })
        });

        const corpo = await resp.text();
        let resultado = null;
        try { resultado = JSON.parse(corpo); } catch { resultado = null; }

        if (!resp.ok) {
            const msg = resultado?.detail || resultado?.message || resultado?.error || `Erro HTTP ${resp.status}.`;
            showToast(msg, "error");
            return;
        }

        showToast("Cliente criado com sucesso!", "success");
        fecharModal(modalUsuario);
        await carregarDashboard();

    } catch (err) {
        showToast(err instanceof TypeError ? "Não foi possível conectar ao backend." : err.message, "error");
    } finally {
        btn.disabled = false;
        btn.textContent = "Criar Conta";
    }
});

/* ──────────────────────────────────────────────────────────
   14. ORDENAÇÃO
────────────────────────────────────────────────────────── */
document.querySelectorAll(".th-sortable").forEach(th => {
    th.addEventListener("click", () => {
        const field = th.dataset.sort;
        if (sortField === field) {
            sortAsc = !sortAsc;
        } else {
            sortField = field;
            sortAsc = true;
        }

        // Atualiza visual
        document.querySelectorAll(".th-sortable").forEach(h => {
            h.classList.remove("th-asc", "th-desc");
        });
        th.classList.add(sortAsc ? "th-asc" : "th-desc");

        // Re-ordena e re-renderiza
        paginaAtual = 1;
        renderComPaginacao(listaFiltrada);
    });
});

function ordenar(lista) {
    return [...lista].sort((a, b) => {
        let va = a[sortField] ?? "";
        let vb = b[sortField] ?? "";
        if (typeof va === "number" && typeof vb === "number") {
            return sortAsc ? va - vb : vb - va;
        }
        va = String(va).toLowerCase();
        vb = String(vb).toLowerCase();
        return sortAsc ? va.localeCompare(vb) : vb.localeCompare(va);
    });
}

/* ──────────────────────────────────────────────────────────
   15. PAGINAÇÃO
────────────────────────────────────────────────────────── */
function renderComPaginacao(lista) {
    const ordenada = ordenar(lista);
    const total    = ordenada.length;
    const totalPag = Math.max(1, Math.ceil(total / POR_PAGINA));

    // Corrige página fora do range
    if (paginaAtual > totalPag) paginaAtual = totalPag;

    const inicio = (paginaAtual - 1) * POR_PAGINA;
    const fim    = Math.min(inicio + POR_PAGINA, total);
    const pagina = ordenada.slice(inicio, fim);

    renderizarTabela(pagina);

    // Info de paginação
    if (elPagInfo) {
        elPagInfo.textContent = total === 0
            ? "Nenhum resultado"
            : `Exibindo ${inicio + 1}–${fim} de ${total} registros`;
    }

    // Botões prev/next
    if (btnPagAnterior) btnPagAnterior.disabled = paginaAtual === 1;
    if (btnPagProximo)  btnPagProximo.disabled  = paginaAtual === totalPag;

    // Botões de número
    if (elPagPages) {
        elPagPages.innerHTML = "";
        const max = Math.min(totalPag, 7);
        let startPage = Math.max(1, paginaAtual - 3);
        let endPage   = Math.min(totalPag, startPage + max - 1);
        if (endPage - startPage < max - 1) startPage = Math.max(1, endPage - max + 1);

        for (let p = startPage; p <= endPage; p++) {
            const btn = document.createElement("button");
            btn.className = `btn-pag-num${p === paginaAtual ? " active" : ""}`;
            btn.textContent = p;
            btn.addEventListener("click", () => {
                paginaAtual = p;
                renderComPaginacao(listaFiltrada);
            });
            elPagPages.appendChild(btn);
        }
    }
}

// Exporta para uso nos scripts inline do HTML
window._renderComPaginacao = renderComPaginacao;

btnPagAnterior?.addEventListener("click", () => {
    if (paginaAtual > 1) { paginaAtual--; renderComPaginacao(listaFiltrada); }
});

btnPagProximo?.addEventListener("click", () => {
    const totalPag = Math.ceil(listaFiltrada.length / POR_PAGINA);
    if (paginaAtual < totalPag) { paginaAtual++; renderComPaginacao(listaFiltrada); }
});

/* ──────────────────────────────────────────────────────────
   16. BUSCA + FILTROS
────────────────────────────────────────────────────────── */
function aplicarFiltros() {
    const busca  = (elBusca?.value || "").toLowerCase().trim();
    const status = document.getElementById("filtro-status")?.value || "";
    const tipo   = document.getElementById("filtro-tipo")?.value || "";

    listaFiltrada = listaUsuarios.filter(u => {
        const matchBusca  = !busca
            || (u.nome  || "").toLowerCase().includes(busca)
            || (u.email || "").toLowerCase().includes(busca)
            || String(u.id).includes(busca);
        const matchStatus = !status || (u.status      || "").toUpperCase() === status;
        const matchTipo   = !tipo   || (u.tipoUsuario || "").toUpperCase() === tipo;
        return matchBusca && matchStatus && matchTipo;
    });

    paginaAtual = 1;
    renderComPaginacao(listaFiltrada);
}

elBusca?.addEventListener("input", aplicarFiltros);
document.getElementById("filtro-status")?.addEventListener("change", aplicarFiltros);
document.getElementById("filtro-tipo")?.addEventListener("change", aplicarFiltros);

/* ──────────────────────────────────────────────────────────
   17. RENDERIZAR TABELA PRINCIPAL
────────────────────────────────────────────────────────── */
function renderizarTabela(lista) {
    if (!lista.length) {
        elTbody.innerHTML = `<tr><td colspan="7" class="td-empty">Nenhum registro encontrado.</td></tr>`;
        return;
    }

    elTbody.innerHTML = lista.map(u => {
        const ativo    = (u.status || "").toUpperCase() === "ATIVO";
        const stClass  = ativo ? "status-ativo" : "status-inativo";
        const stLabel  = u.status || "—";
        const tipo     = u.tipoUsuario || "—";

        const acoesAdmin = isAdmin ? `
            <td class="th-acoes">
                <div class="table-actions">
                    <button class="btn-table-action btn-table-tipo"
                        title="Alternar tipo de perfil"
                        onclick="event.stopPropagation(); alterarTipoRapido(${u.id}, '${u.tipoUsuario}')">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                            stroke-linecap="round" stroke-linejoin="round" width="13" height="13">
                            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                        </svg>
                    </button>
                    ${ativo
                        ? `<button class="btn-table-action btn-table-desativar"
                                title="Desativar conta"
                                onclick="event.stopPropagation(); ativarDesativar(${u.id}, false)">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                                    stroke-linecap="round" stroke-linejoin="round" width="13" height="13">
                                    <circle cx="12" cy="12" r="10"/>
                                    <line x1="4.93" y1="4.93" x2="19.07" y2="19.07"/>
                                </svg>
                           </button>`
                        : `<button class="btn-table-action btn-table-ativar"
                                title="Ativar conta"
                                onclick="event.stopPropagation(); ativarDesativar(${u.id}, true)">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                                    stroke-linecap="round" stroke-linejoin="round" width="13" height="13">
                                    <polyline points="20 6 9 17 4 12"/>
                                </svg>
                           </button>`
                    }
                    <button class="btn-table-action btn-table-deletar"
                        title="Excluir cliente"
                        onclick="event.stopPropagation(); deletarUsuario(${u.id}, '${(u.nome || '').replace(/'/g, "\\'")}')">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                            stroke-linecap="round" stroke-linejoin="round" width="13" height="13">
                            <polyline points="3 6 5 6 21 6"/>
                            <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/>
                            <path d="M10 11v6"/><path d="M14 11v6"/>
                        </svg>
                    </button>
                </div>
            </td>` : "";

        return `
            <tr data-usuario-id="${u.id}">
                <td>${u.id ?? "—"}</td>
                <td class="td-name">${u.nome ?? "—"}</td>
                <td>${u.email ?? "—"}</td>
                <td>${u.telefone ?? "—"}</td>
                <td>${tipo}</td>
                <td><span class="status-badge ${stClass}">${stLabel}</span></td>
                ${acoesAdmin}
            </tr>`;
    }).join("");
}

/* ──────────────────────────────────────────────────────────
   18. TABELA RESUMIDA (Visão Geral)
────────────────────────────────────────────────────────── */
function renderizarRecentes(lista) {
    if (!elTbodyRecentes) return;
    const recentes = lista.slice(0, 5);
    if (!recentes.length) {
        elTbodyRecentes.innerHTML = `<tr><td colspan="4" class="td-empty">Nenhum registro.</td></tr>`;
        return;
    }
    elTbodyRecentes.innerHTML = recentes.map(u => {
        const ativo   = (u.status || "").toUpperCase() === "ATIVO";
        const stClass = ativo ? "status-ativo" : "status-inativo";
        return `
            <tr>
                <td class="td-name">${u.nome ?? "—"}</td>
                <td>${u.email ?? "—"}</td>
                <td>${u.tipoUsuario ?? "—"}</td>
                <td><span class="status-badge ${stClass}">${u.status ?? "—"}</span></td>
            </tr>`;
    }).join("");
}

/* ──────────────────────────────────────────────────────────
   19. AÇÕES GLOBAIS (chamadas por onclick nos botões da tabela)
────────────────────────────────────────────────────────── */
window.ativarDesativar = function(id, ativar) {
    const acao  = ativar ? "ativar" : "desativar";
    const label = ativar ? "ativar" : "desativar";
    confirmar(
        `Deseja ${label} o cliente ID ${id}?`,
        () => executarPatch(id, acao),
        ativar ? "btn-success" : "btn-warning"
    );
};

window.deletarUsuario = function(id, nome) {
    confirmar(
        `Deseja excluir permanentemente "${nome}" (ID ${id})?\nEsta ação não pode ser desfeita.`,
        () => executarDelete(id),
        "btn-danger"
    );
};

window.alterarTipoRapido = function(id, tipoAtual) {
    const novoTipo = tipoAtual === "ADMINISTRADOR" ? "USUARIO" : "ADMINISTRADOR";
    const label    = novoTipo === "ADMINISTRADOR"  ? "Administrador" : "Usuário";
    confirmar(
        `Alterar perfil do cliente ID ${id} para ${label}?`,
        () => executarAlterarTipo(id, novoTipo),
        "btn-info"
    );
};

/* ──────────────────────────────────────────────────────────
   20. CHAMADAS À API
────────────────────────────────────────────────────────── */
async function executarPatch(id, acao) {
    try {
        const resp = await fetch(`${API_BASE_URL}/usuarios/${id}/${acao}`, {
            method: "PATCH",
            headers: { Accept: "application/json" }
        });
        if (!resp.ok) throw new Error(`Erro HTTP ${resp.status}`);
        showToast(`Cliente ${acao === "ativar" ? "ativado" : "desativado"} com sucesso!`, "success");
        await carregarDashboard();
    } catch (err) {
        showToast("Erro ao atualizar status: " + err.message, "error");
    }
}

async function executarDelete(id) {
    try {
        const resp = await fetch(`${API_BASE_URL}/usuarios/${id}`, {
            method: "DELETE",
            headers: { Accept: "application/json" }
        });
        if (!resp.ok) throw new Error(`Erro HTTP ${resp.status}`);
        showToast("Cliente excluído com sucesso!", "success");
        await carregarDashboard();
    } catch (err) {
        showToast("Erro ao excluir: " + err.message, "error");
    }
}

async function executarAlterarTipo(id, novoTipo) {
    try {
        const respGet = await fetch(`${API_BASE_URL}/usuarios/${id}`, {
            headers: { Accept: "application/json" }
        });
        if (!respGet.ok) throw new Error(`Erro ao buscar usuário: HTTP ${respGet.status}`);
        const usuario = await respGet.json();

        const resp = await fetch(`${API_BASE_URL}/usuarios/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json", Accept: "application/json" },
            body: JSON.stringify({ ...usuario, tipoUsuario: novoTipo })
        });
        if (!resp.ok) throw new Error(`Erro HTTP ${resp.status}`);
        showToast("Perfil atualizado com sucesso!", "success");
        await carregarDashboard();
    } catch (err) {
        showToast("Erro ao alterar perfil: " + err.message, "error");
    }
}

/* ──────────────────────────────────────────────────────────
   21. PAINEL ADMIN — botões de ação rápida
────────────────────────────────────────────────────────── */
document.querySelectorAll(".btn-action[data-acao]").forEach(btn => {
    btn.addEventListener("click", () => {
        const acao = btn.dataset.acao;

        if (acao === "ativar") {
            const id = parseInt(inputIdAtivar?.value);
            if (!id) { showToast("Informe um ID válido.", "error"); return; }
            confirmar(`Ativar o cliente ID ${id}?`, () => executarPatch(id, "ativar"), "btn-success");
        }
        if (acao === "desativar") {
            const id = parseInt(inputIdDesativar?.value);
            if (!id) { showToast("Informe um ID válido.", "error"); return; }
            confirmar(`Desativar o cliente ID ${id}?`, () => executarPatch(id, "desativar"), "btn-warning");
        }
        if (acao === "tipo") {
            const id   = parseInt(inputIdTipo?.value);
            const tipo = selectTipo?.value;
            if (!id)   { showToast("Informe um ID válido.", "error"); return; }
            if (!tipo) { showToast("Selecione o tipo.", "error"); return; }
            confirmar(`Alterar perfil do cliente ID ${id} para ${tipo}?`,
                () => executarAlterarTipo(id, tipo), "btn-info");
        }
        if (acao === "deletar") {
            const id = parseInt(inputIdDeletar?.value);
            if (!id) { showToast("Informe um ID válido.", "error"); return; }
            confirmar(`Excluir permanentemente o cliente ID ${id}? Esta ação não pode ser desfeita.`,
                () => executarDelete(id), "btn-danger");
        }
    });
});

/* ──────────────────────────────────────────────────────────
   22. CARREGAR DASHBOARD (função principal)
────────────────────────────────────────────────────────── */
async function carregarDashboard() {
    elLoading.classList.remove("hidden");
    elErro.hidden = true;
    if (elBusca) elBusca.value = "";

    try {
        const resp = await fetch(`${API_BASE_URL}/usuarios`, {
            headers: { Accept: "application/json" }
        });

        if (!resp.ok) throw new Error(`Erro HTTP ${resp.status} ao buscar usuários.`);

        listaUsuarios = await resp.json();
        listaFiltrada = [...listaUsuarios];

        // Expõe para scripts inline
        window._listaUsuarios = listaUsuarios;

        // Métricas
        elTotal.textContent    = listaUsuarios.length;
        elAtivos.textContent   = listaUsuarios.filter(u => (u.status      || "").toUpperCase() === "ATIVO").length;
        elInativos.textContent = listaUsuarios.filter(u => (u.status      || "").toUpperCase() !== "ATIVO").length;
        elAdmins.textContent   = listaUsuarios.filter(u => (u.tipoUsuario || "").toUpperCase() === "ADMINISTRADOR").length;

        // Tabela principal com paginação
        paginaAtual = 1;
        renderComPaginacao(listaFiltrada);

        // Tabela resumida na Visão Geral
        renderizarRecentes(listaUsuarios);

    } catch (err) {
        const msg = err instanceof TypeError
            ? "Não foi possível conectar ao backend. Verifique se o servidor está ativo."
            : err.message;

        elErro.textContent = msg;
        elErro.hidden = false;

        elTotal.textContent = elAtivos.textContent =
            elInativos.textContent = elAdmins.textContent = "—";
    } finally {
        elLoading.classList.add("hidden");
    }
}

// Inicializa
carregarDashboard();
