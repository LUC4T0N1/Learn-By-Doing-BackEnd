package dominio;

import infraestrutura.dto.UsuarioDto;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Entity(name = "usuario")
public class Usuario extends ObjetoDeDominio{

    @Column
    private String email;

    @Column
    private String nome;

    @Column
    private String senha;


    protected Usuario(){}

    public static Usuario instanciar(UsuarioDto dto) {
        UUID uuid = UUID.randomUUID();
        String usuario = uuid.toString();
        Usuario usuarioObj = new Usuario();
        usuarioObj.setUsuario(usuario);
        usuarioObj.setEmail(dto.email);
        usuarioObj.setNome(dto.nome);
        usuarioObj.setSenha(cryptografarSenha(dto.senha));
        return usuarioObj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public static String cryptografarSenha(String senha){
        String encryptedpassword = null;
        try {
            /* MessageDigest instance for MD5. */
            MessageDigest m = MessageDigest.getInstance("MD5");

            /* Add plain-text password bytes to digest using MD5 update() method. */
            m.update(senha.getBytes());

            /* Convert the hash value into bytes */
            byte[] bytes = m.digest();

            /* The bytes array has bytes in decimal form. Converting it into hexadecimal format. */
            StringBuilder s = new StringBuilder();
            for (byte aByte : bytes) {
                s.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            /* Complete hashed password in hexadecimal format */
            encryptedpassword = s.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return encryptedpassword;
    }

}
