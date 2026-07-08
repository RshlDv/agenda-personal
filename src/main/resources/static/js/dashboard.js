function abrirModalAgregar() {
    document.getElementById('modalTitle').innerText = "Nuevo Evento";
    document.getElementById('modalForm').action = "/eventos";
    document.getElementById('modalForm').reset();
    document.getElementById('modalEvento').classList.remove('hidden');
}

function abrirModalEditar(btn) {
    document.getElementById('modalTitle').innerText = "Editar Evento";
    document.getElementById('modalForm').action = "/eventos/editar/" + btn.getAttribute('data-id');

    document.getElementById('insTitulo').value = btn.getAttribute('data-titulo');
    document.getElementById('insDesc').value = btn.getAttribute('data-desc');
    document.getElementById('insFecha').value = btn.getAttribute('data-fecha');
    document.getElementById('insHora').value = btn.getAttribute('data-hora');
    document.getElementById('insUbicacion').value = btn.getAttribute('data-ubicacion');
    document.getElementById('insNota').value = btn.getAttribute('data-nota');
    document.getElementById('insCategoria').value = btn.getAttribute('data-categoria');

    document.getElementById('modalEvento').classList.remove('hidden');
}

function cerrarModal() {
    document.getElementById('modalEvento').classList.add('hidden');
}