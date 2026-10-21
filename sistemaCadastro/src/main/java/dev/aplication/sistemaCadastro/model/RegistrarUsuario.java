package dev.aplication.sistemaCadastro.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "registrar_usuario")
public class RegistrarUsuario {

    @Id
    @Column(name = "usuario_id")
    private Integer id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "email", length = 120, unique = true, nullable = false)
    private String email;

    @Column(name = "senha", length = 255, nullable = false)
    private String senha;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    public RegistrarUsuario() {
    }

    public RegistrarUsuario(Usuario usuario, String email, String senha, Boolean ativo) {
        this.usuario = usuario;
        this.email = email;
        this.senha = senha;
        this.ativo = ativo != null ? ativo : true;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}