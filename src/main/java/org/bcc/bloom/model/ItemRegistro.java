
package org.bcc.bloom.model;

import java.math.BigDecimal;

public record ItemRegistro(
        long id,
        long produtoId,
        int quantidade,
        BigDecimal valorUnitario
) {}
