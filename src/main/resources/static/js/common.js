document.addEventListener('DOMContentLoaded', function() {
    // 1. Iestata dinamisko datumu augšējā joslā latviešu valodā
    const dateDisplay = document.getElementById('currentDateDisplay');
    if (dateDisplay) {
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        const todayStr = new Intl.DateTimeFormat('lv-LV', options).format(new Date());
        dateDisplay.innerText = `Šodien: ${todayStr}`;
    }

    // 2. Inicializē datuma laukus, lietotājam parādot Latvijas standartu
    flatpickr("input[type='date']", {
        locale: "lv",
        altInput: true,
        altFormat: "d.m.Y", // Vizuālais formāts lietotājam
        dateFormat: "Y-m-d" // Fona formāts standartizētai datu sūtīšanai
    });
});