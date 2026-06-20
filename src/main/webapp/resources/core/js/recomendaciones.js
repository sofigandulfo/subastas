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
        var card = document.createElement("article");
        card.className = "app-card subasta-card";

        var header = document.createElement("div");
        header.className = "app-card-body";

        var imgContainer = document.createElement("div");
        imgContainer.className = "subasta-image";

        if (subasta.imagen) {
          var img = document.createElement("img");
          img.src = "data:image/jpeg;base64," + subasta.imagen;
          img.alt = "Imagen del producto";
          img.className = "subasta-image";
          imgContainer.appendChild(img);
        } else {
          imgContainer.classList.add("subasta-image-placeholder");
          imgContainer.textContent = "Sin imagen";
        }

        var body = document.createElement("div");
        body.className = "app-card-body";

        var titulo = document.createElement("h3");
        titulo.className = "subasta-title";
        titulo.textContent = subasta.nombre;

        var desc = document.createElement("p");
        desc.className = "subasta-description";
        desc.textContent = subasta.descripcion;

        var precio = document.createElement("p");
        precio.className = "price";
        precio.textContent = "$" + subasta.precioActual;

        var categoria = document.createElement("p");
        categoria.className = "text-muted";
        categoria.textContent = subasta.categoria;

        var link = document.createElement("a");
        link.href = "/spring/detalle-subasta?id=" + subasta.id;
        link.className = "btn btn-app-primary w-100";
        link.textContent = "Ver detalle";

        header.appendChild(titulo);
        body.appendChild(desc);
        body.appendChild(precio);
        body.appendChild(categoria);
        body.appendChild(link);
        card.appendChild(header);
        card.appendChild(imgContainer);
        card.appendChild(body);
        contenedor.appendChild(card);
      });
    })
    .catch(function () {
      document.getElementById("seccion-recomendadas").style.display = "none";
    });
});
