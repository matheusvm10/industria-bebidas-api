package br.com.senai.bebidas.service;

import br.com.senai.bebidas.dto.request.LoginRequest;
import br.com.senai.bebidas.dto.request.UsuarioCreateRequest;
import br.com.senai.bebidas.dto.request.UsuarioUpdateRequest;
import br.com.senai.bebidas.dto.response.TokenResponse;
import br.com.senai.bebidas.dto.response.UsuarioResponse;
import br.com.senai.bebidas.entity.Usuario;
import br.com.senai.bebidas.exception.BusinessConflictException;
import br.com.senai.bebidas.exception.ResourceNotFoundException;
import br.com.senai.bebidas.exception.UnauthorizedException;
import br.com.senai.bebidas.repository.UsuarioRepository;
import br.com.senai.bebidas.security.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public UsuarioResponse cadastrar(UsuarioCreateRequest req) {
        if (usuarioRepository.existsByLogin(req.login().trim())) {
            throw new BusinessConflictException("Login já cadastrado");
        }
        Usuario usuario = new Usuario(
                req.nome().trim(),
                req.login().trim(),
                passwordEncoder.encode(req.senha())
        );
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public TokenResponse autenticar(LoginRequest req) {
        Usuario usuario = usuarioRepository.findByLogin(req.login().trim())
                .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas"));

        if (!Boolean.TRUE.equals(usuario.getAtivo()) ||
                !passwordEncoder.matches(req.senha(), usuario.getSenhaHash())) {
            throw new UnauthorizedException("Credenciais inválidas");
        }

        String token = jwtService.gerarToken(usuario);
        return new TokenResponse(token, "Bearer", jwtService.getExpirationSeconds());
    }

    @Transactional(readOnly = true)
    public Usuario getUsuarioAutenticadoEntity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Usuario principal)) {
            throw new UnauthorizedException("Autenticação necessária");
        }
        return usuarioRepository.findById(principal.getId())
                .filter(u -> Boolean.TRUE.equals(u.getAtivo()))
                .orElseThrow(() -> new UnauthorizedException("Autenticação necessária"));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarMe() {
        return UsuarioResponse.from(getUsuarioAutenticadoEntity());
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar(String ativoParam) {
        Boolean ativo = FiltroAtivoHelper.resolver(ativoParam);
        List<Usuario> lista = (ativo == null)
                ? usuarioRepository.findAllByOrderByIdAsc()
                : usuarioRepository.findByAtivoOrderByIdAsc(ativo);
        return lista.stream().map(UsuarioResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioUpdateRequest req) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        String novoLogin = req.login().trim();
        if (usuarioRepository.existsByLoginAndIdNot(novoLogin, id)) {
            throw new BusinessConflictException("Login já cadastrado");
        }

        usuario.setNome(req.nome().trim());
        usuario.setLogin(novoLogin);
        usuario.setAtivo(req.ativo());
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional
    public void inativar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }
}
