document.addEventListener("DOMContentLoaded", function () {
    const formularioOferta = document.getElementById("form-oferta");
    const botonOfertar = document.getElementById("btn-ofertar");
    const botonConfirmarOferta = document.getElementById("btn-confirmar-oferta");
    const inputMonto = document.getElementById("monto");
    const montoConfirmacion = document.getElementById("monto-confirmacion");
    const modalConfirmarOfertaElemento = document.getElementById("modalConfirmarOferta");

    if (!formularioOferta || !botonOfertar || !botonConfirmarOferta || !inputMonto || !montoConfirmacion || !modalConfirmarOfertaElemento) {
        return;
    }

    const modalConfirmarOferta = new bootstrap.Modal(modalConfirmarOfertaElemento);

    botonOfertar.addEventListener("click", function () {
        if (!inputMonto.checkValidity()) {
            inputMonto.reportValidity();
            return;
        }

        montoConfirmacion.textContent = "$" + inputMonto.value;
        modalConfirmarOferta.show();
    });

    botonConfirmarOferta.addEventListener("click", function () {
        formularioOferta.submit();
    });
});