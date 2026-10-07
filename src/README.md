# Sistema de Cadastro

Interface web responsiva para autenticação e registro de usuários. O projeto reúne as telas de login e cadastro em uma única página e valida os dados localmente no navegador.

> **Status:** protótipo de front-end. Não há integração com back-end nem persistência de contas; os dados informados não são enviados ou armazenados.

## Funcionalidades

- Alternância entre as telas de login e cadastro na mesma página.
- Transições suaves entre as telas, respeitando a preferência do sistema por movimento reduzido.
- Layout responsivo para diferentes larguras e alturas de tela.
- Validação dos campos de login:
  - preenchimento de e-mail e senha;
  - formato do endereço de e-mail.
- Validação dos campos de cadastro:
  - nome, e-mail, senha e confirmação da senha;
  - formato do endereço de e-mail;
  - correspondência entre as senhas;
  - seleção do tipo de usuário.
- Notificações toast para exibir erros e informar que a autenticação e o cadastro ainda não estão conectados ao back-end.
- Campo de seleção do tipo de usuário: administrador, usuário ou visitante.

## Tecnologias

- HTML5
- CSS3
- JavaScript
- Fonte Commissioner, carregada do Google Fonts

## Estrutura

```text
src/
├── assets/
│   ├── IconCadastroLogo.ico
│   └── IconCadastroLogo.png
├── index.css
├── index.html
├── index.js
└── README.md
```

## Como executar

1. Abra `src/index.html` em um navegador.
2. Para desenvolvimento, também é possível servir a pasta `src/` por um servidor HTTP local.
3. Use **Registrar** para abrir o formulário de cadastro e **Voltar ao login** para retornar.

É necessária uma conexão com a internet para carregar a fonte Commissioner do Google Fonts. Sem ela, o navegador usará uma fonte genérica alternativa.

## Validações e integração

As validações são executadas no cliente e servem para orientar o preenchimento dos formulários. Elas não substituem validações no servidor. Para habilitar login e criação de contas, será necessário implementar um back-end, conectar os formulários a uma API e definir como as contas serão persistidas e protegidas.
