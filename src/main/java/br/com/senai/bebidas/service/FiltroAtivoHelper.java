package br.com.senai.bebidas.service;

import br.com.senai.bebidas.exception.BadRequestException;

public final class FiltroAtivoHelper {

    private FiltroAtivoHelper() {}

    public static Boolean resolver(String ativoParam) {
        if (ativoParam == null || ativoParam.isBlank() || "true".equalsIgnoreCase(ativoParam)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(ativoParam)) {
            return Boolean.FALSE;
        }
        if ("todos".equalsIgnoreCase(ativoParam)) {
            return null;
        }
        throw new BadRequestException("O parâmetro ativo deve ser true, false ou todos");
    }
}
