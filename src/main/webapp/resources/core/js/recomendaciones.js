document.addEventListener("DOMContentLoaded", function () {
  fetch("/spring/api/recomendaciones")
    .then(function (res) {
      if (res.status === 401) {
        document.getElementById("seccion-recomendadas").style.display = "none";
        return null;
      }
      return res.json();
    })
    .then(function (subastas) {
      if (!subastas || subastas.length === 0) {
        document.getElementById("seccion-recomendadas").style.display = "none";
        return;
      }
      var contenedor = document.getElementById("contenedor-recomendadas");
      contenedor.innerHTML = "";
      subastas.forEach(function (subasta) {
        var col = document.createElement("div");
        col.className = "w3-third w3-margin-bottom";

        var card = document.createElement("div");
        card.className = "w3-card-4 w3-white w3-round-large";

        var header = document.createElement("div");
        header.className = "w3-container w3-blue w3-round-top";
        var titulo = document.createElement("h4");
        titulo.className = "w3-text-white";
        titulo.textContent = subasta.nombre;
        header.appendChild(titulo);

        var imgContainer = document.createElement("div");
        imgContainer.className = "w3-center w3-border-bottom";
        imgContainer.style = "height: 200px; overflow: hidden; display: flex; justify-content: center;" +
            "align-items: center; background-color: #f1f1f1;";

        if (subasta.imagen) {
          var img = document.createElement("img");
          img.src = "data:image/jpeg;base64," + subasta.imagen;
          img.alt = "Imagen del producto";
          img.style = "max-width: 100%; max-height: 100%; object-fit: contain;";
          imgContainer.appendChild(img);
        } else {
          var sinImagenDiv = document.createElement("div");
          sinImagenDiv.className = "w3-text-grey";
          sinImagenDiv.innerHTML = "<p><i class='w3-large'>Sin imagen</i></p>";
          imgContainer.appendChild(sinImagenDiv);
        }

        var body = document.createElement("div");
        body.className = "w3-container w3-padding-16";

        var desc = document.createElement("p");
        desc.className = "w3-text-grey";
        desc.textContent = subasta.descripcion;

        var precio = document.createElement("p");
        precio.innerHTML = "<b>Precio actual:</b> $" + subasta.precioActual;

        var categoria = document.createElement("p");
        categoria.innerHTML = "<b>Categoría:</b> " + subasta.categoria;

        var link = document.createElement("a");
        link.href = "/spring/detalle-subasta?id=" + subasta.id;
        link.className = "w3-button w3-blue w3-round w3-block w3-hover-dark-grey w3-margin-top";
        link.textContent = "Ver detalle";

        body.appendChild(desc);
        body.appendChild(precio);
        body.appendChild(categoria);
        body.appendChild(link);
        card.appendChild(header);
        card.appendChild(imgContainer);
        card.appendChild(body);
        col.appendChild(card);
        contenedor.appendChild(col);
      });
    })
    .catch(function () {
      document.getElementById("seccion-recomendadas").style.display = "none";
    });
});