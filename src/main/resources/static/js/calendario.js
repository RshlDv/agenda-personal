document.addEventListener('DOMContentLoaded', function () {
    var calendarEl = document.getElementById('calendar');
    var calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'Es',
        selectable: true,
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: ''
        },
        select: function (info) {
            document.getElementById('modalFecha').value = info.startStr;
            document.getElementById('modalEvento').classList.remove('hidden');
        }
    });
    calendar.render();
});

function cerrarModal() {
    document.getElementById('modalEvento').classList.add('hidden');
}