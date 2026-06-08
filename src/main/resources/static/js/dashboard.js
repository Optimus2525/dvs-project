document.addEventListener('DOMContentLoaded', function() {
    // 1. Ielasa reālo Entra ID lietotāju no slēptā lauka
    const currentUser = document.getElementById('loggedInUser') ? document.getElementById('loggedInUser').value : '';

    // ==========================================
    // ENTRA ID LIETOTĀJU MEKLĒTĀJU INICIALIZĀCIJA
    // ==========================================

    // 1. Uzdevuma Atbildīgais (Single-select)
    const assigneeSelector = new EntraUserSelector({
        inputId: 'taskAssignee',
        datalistId: 'assigneeList',
        isMultiple: false,
        alertBoxId: 'createTaskAlert'
    });

    // 2. Uzdevuma Sekotāji (Multi-select)
    const followersSelector = new EntraUserSelector({
        inputId: 'taskFollowerInput',
        datalistId: 'followerList',
        pillsContainerId: 'followersPills',
        isMultiple: true,
        alertBoxId: 'createTaskAlert'
    });

    // 3. Kalendāra Uzaicinātās personas (Multi-select + Konfliktu pārbaude)
    const inviteesSelector = new EntraUserSelector({
        inputId: 'eventInviteeInput',
        datalistId: 'inviteeList',
        pillsContainerId: 'inviteesPills',
        isMultiple: true,
        alertBoxId: 'eventAlert',
        onUserAdded: async (userName, badgeElement) => {
            const sVal = document.getElementById('eventStart').value;
            const eVal = document.getElementById('eventEnd').value;
            if (sVal && eVal) {
                try {
                    const formattedStart = sVal.length <= 10 ? sVal + "T00:00:00" : sVal;
                    const formattedEnd = eVal.length <= 10 ? eVal + "T00:00:00" : eVal;
                    const res = await fetch(`/api/v1/calendar/events/check-conflict?user=${encodeURIComponent(userName)}&start=${formattedStart}&end=${formattedEnd}`);
                    const hasConflict = await res.json();

                    if (hasConflict) {
                        const warningIcon = document.createElement('i');
                        warningIcon.className = 'text-danger ms-1 small';
                        warningIcon.title = 'Šim lietotājam jau ir ieplānots cits notikums šajā laikā!';
                        warningIcon.innerText = '(Aizņemts)';
                        const closeBtn = badgeElement.querySelector('span.ms-2');
                        badgeElement.insertBefore(warningIcon, closeBtn);
                    }
                } catch (err) { console.error("Konfliktu pārbaudes kļūda", err); }
            }
        }
    });

    // ==========================================
    // KALENDĀRA LOĢIKA
    // ==========================================
    const calendarWrapper = document.getElementById('calendarWrapper');
    const calendarGrid = document.getElementById('calendarGrid');
    const currentMonthLabel = document.getElementById('currentMonthLabel');
    const tasksContainer = document.getElementById('tasksContainer');
    const eventModalEl = document.getElementById('eventModal');
    const eventModal = eventModalEl ? new bootstrap.Modal(eventModalEl) : null;
    const monthNames = ["Janvāris", "Februāris", "Marts", "Aprīlis", "Maijs", "Jūnijs", "Jūlijs", "Augusts", "Septembris", "Oktobris", "Novembris", "Decembris"];

    let currentDate = new Date();
    let events = [];
    let currentView = 'week';
    let fpStart, fpEnd;

    if(calendarWrapper) {
        document.querySelectorAll('.view-link').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                document.querySelectorAll('.view-link').forEach(l => {
                    l.classList.remove('text-primary', 'fw-bold');
                    l.classList.add('text-muted');
                });
                e.currentTarget.classList.remove('text-muted');
                e.currentTarget.classList.add('text-primary', 'fw-bold');

                currentView = e.currentTarget.getAttribute('data-view');
                if (currentView === 'day') tasksContainer.style.display = 'none';
                else tasksContainer.style.display = 'block';

                renderCalendar();
            });
        });

        function initEventFlatpickr(enableTime) {
            if (fpStart) fpStart.destroy();
            if (fpEnd) fpEnd.destroy();
            const fpConfig = { locale: "lv", enableTime: enableTime, time_24hr: true, altInput: true, altFormat: enableTime ? "d.m.Y H:i" : "d.m.Y", dateFormat: enableTime ? "Y-m-d\\TH:i" : "Y-m-d", firstDayOfWeek: 1 };
            fpStart = flatpickr("#eventStart", fpConfig);
            fpEnd = flatpickr("#eventEnd", fpConfig);
        }

        document.getElementById('eventAllDay').addEventListener('change', function() {
            const sVal = document.getElementById('eventStart').value;
            const eVal = document.getElementById('eventEnd').value;
            initEventFlatpickr(!this.checked);
            if (sVal) fpStart.setDate(sVal);
            if (eVal) fpEnd.setDate(eVal);
        });

        function formatPopoverDate(startStr, endStr, isAllDay) {
            const s = new Date(startStr); const e = new Date(endStr);
            const pad = (n) => n.toString().padStart(2, '0');
            const sDate = `${pad(s.getDate())}.${pad(s.getMonth()+1)}.`;
            const eDate = `${pad(e.getDate())}.${pad(e.getMonth()+1)}.`;
            const sTime = `${pad(s.getHours())}:${pad(s.getMinutes())}`;
            const eTime = `${pad(e.getHours())}:${pad(e.getMinutes())}`;
            if (isAllDay) return (sDate === eDate) ? sDate : `${sDate} - ${eDate}`;
            else return (sDate === eDate) ? `${sDate} ${sTime} - ${eTime}` : `${sDate} ${sTime} - ${eDate} ${eTime}`;
        }

        function createEventElement(e, mode = 'default') {
            const eventDiv = document.createElement('div');
            eventDiv.style.backgroundColor = e.categoryColor;
            eventDiv.className = (mode === 'day-timed' && !e.allDay) ? 'day-absolute-event' : 'calendar-event shadow-sm mb-1';

            const iconSvg = e.allDay
                ? `<svg xmlns="http://www.w3.org/2000/svg" width="11" height="11" fill="currentColor" viewBox="0 0 16 16" style="vertical-align: -0.125em;"><path d="M3.5 0a.5.5 0 0 1 .5.5V1h8V.5a.5.5 0 0 1 1 0V1h1a2 2 0 0 1 2 2v11a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V3a2 2 0 0 1 2-2h1V.5a.5.5 0 0 1 .5-.5zM1 4v10a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1V4H1z"/></svg>`
                : `<svg xmlns="http://www.w3.org/2000/svg" width="11" height="11" fill="currentColor" viewBox="0 0 16 16" style="vertical-align: -0.125em;"><path d="M8 3.5a.5.5 0 0 0-1 0V9a.5.5 0 0 0 .252.434l3.5 2a.5.5 0 0 0 .496-.868L8 8.71V3.5z"/><path d="M8 16A8 8 0 1 0 8 0a8 8 0 0 0 0 16zm7-8A7 7 0 1 1 1 8a7 7 0 0 1 14 0z"/></svg>`;

            eventDiv.innerHTML = `<span class="me-1 opacity-75">${iconSvg}</span>`;
            eventDiv.appendChild(document.createTextNode(e.title));

            if (mode === 'day-timed' && !e.allDay) {
                const pad = (n) => n.toString().padStart(2, '0');
                const s = new Date(e.startTime); const en = new Date(e.endTime);
                const timeText = `${pad(s.getHours())}:${pad(s.getMinutes())} - ${pad(en.getHours())}:${pad(en.getMinutes())}`;
                eventDiv.insertAdjacentHTML('beforeend', `<svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" class="mx-1 opacity-75" viewBox="0 0 16 16"><path fill-rule="evenodd" d="M4 8a.5.5 0 0 1 .5-.5h5.793L8.146 5.354a.5.5 0 1 1 .708-.708l3 3a.5.5 0 0 1 0 .708l-3 3a.5.5 0 0 1-.708-.708L10.293 8.5H4.5A.5.5 0 0 1 4 8z"/></svg>`);
                eventDiv.appendChild(document.createTextNode(timeText));
            }

            const formattedDate = formatPopoverDate(e.startTime, e.endTime, e.allDay);
            // ATJAUNOTS: Pievieno Autoru un Uzaicinātās personas Popover lodziņam
            const authorHtml = e.createdBy ? `<br><small class="text-muted"><strong class="text-dark">Autors:</strong> ${e.createdBy}</small>` : '';
            const inviteesHtml = (e.invitedPersons && e.invitedPersons.length > 0) ? `<br><small class="text-muted"><strong class="text-dark">Uzaicinātie:</strong> ${e.invitedPersons.join(', ')}</small>` : '';

            eventDiv.setAttribute('data-bs-toggle', 'popover');
            eventDiv.setAttribute('data-bs-trigger', 'hover focus');
            eventDiv.setAttribute('data-bs-placement', 'top');
            eventDiv.setAttribute('data-bs-title', `<span class="fw-bold">${e.categoryName}</span>`);
            eventDiv.setAttribute('data-bs-html', 'true');
            eventDiv.setAttribute('data-bs-content', `<strong>${e.title}</strong><br><small class="text-muted">${formattedDate}</small>${authorHtml}${inviteesHtml}<br>${e.description ? `<hr class="my-1">${e.description}` : ''}`);

            eventDiv.onclick = (evt) => {
                evt.stopPropagation();
                const popover = bootstrap.Popover.getInstance(eventDiv);
                if (popover) popover.hide();
                openEditEventModal(e);
            };
            return eventDiv;
        }

        async function renderCalendar() {
            calendarGrid.innerHTML = '';
            let startDate, endDate, daysToRender = [];

            if (currentView === 'day') {
                calendarWrapper.classList.add('day-view-mode');
                startDate = new Date(currentDate); endDate = new Date(currentDate);
                currentMonthLabel.innerText = `${startDate.getDate()}. ${monthNames[startDate.getMonth()]}, ${startDate.getFullYear()}`;
                await fetchEvents(startDate, endDate);
                renderDayView(startDate);
                return;
            } else {
                calendarWrapper.classList.remove('day-view-mode');
            }

            if (currentView === 'week') {
                const day = currentDate.getDay();
                const diff = currentDate.getDate() - day + (day === 0 ? -6 : 1);
                const monday = new Date(currentDate); monday.setDate(diff);
                for(let i = 0; i < 7; i++) { const d = new Date(monday); d.setDate(monday.getDate() + i); daysToRender.push(d); }
                startDate = daysToRender[0]; endDate = daysToRender[6];
                currentMonthLabel.innerText = `${monthNames[startDate.getMonth()]}, ${startDate.getFullYear()}`;
            } else {
                const year = currentDate.getFullYear(), month = currentDate.getMonth();
                currentMonthLabel.innerText = `${monthNames[month]}, ${year}`;
                const firstDay = new Date(year, month, 1), lastDay = new Date(year, month + 1, 0);
                let startDayOfWeek = firstDay.getDay() - 1; if (startDayOfWeek === -1) startDayOfWeek = 6;
                for (let i = 0; i < startDayOfWeek; i++) daysToRender.push(null);
                for (let day = 1; day <= lastDay.getDate(); day++) daysToRender.push(new Date(year, month, day));
                startDate = firstDay; endDate = lastDay;
            }

            await fetchEvents(startDate, endDate);
            const today = new Date();

            daysToRender.forEach(cellDate => {
                if (cellDate === null) { calendarGrid.appendChild(Object.assign(document.createElement('div'), {className: 'calendar-cell bg-light opacity-50'})); return; }
                const cell = document.createElement('div');
                cell.className = `calendar-cell ${(cellDate.getDate() === today.getDate() && cellDate.getMonth() === today.getMonth() && cellDate.getFullYear() === today.getFullYear()) ? 'today' : ''}`;
                cell.innerHTML = `<div class="calendar-date ${(cellDate.getDay() === 0 || cellDate.getDay() === 6) ? 'weekend' : ''}">${cellDate.getDate()}</div><a class="add-event-btn" title="Pievienot">+</a>`;
                cell.querySelector('.add-event-btn').onclick = () => openEditEventModal(null, cellDate);

                events.filter(e => cellDate >= new Date(new Date(e.startTime).setHours(0,0,0,0)) && cellDate <= new Date(new Date(e.endTime).setHours(23,59,59,999)))
                    .forEach(e => cell.appendChild(createEventElement(e)));
                calendarGrid.appendChild(cell);
            });

            const popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]');
            [...popoverTriggerList].map(popoverTriggerEl => new bootstrap.Popover(popoverTriggerEl));
        }

        function renderDayView(date) {
            const dayEvents = events.filter(e => date >= new Date(new Date(e.startTime).setHours(0,0,0,0)) && date <= new Date(new Date(e.endTime).setHours(23,59,59,999)));
            const allDayEvents = dayEvents.filter(e => e.allDay);
            if (allDayEvents.length > 0) {
                const allDayContainer = document.createElement('div');
                allDayContainer.className = 'day-view-all-day';
                allDayContainer.innerHTML = '<strong class="small text-muted d-block mb-2">Visas dienas notikumi</strong>';
                allDayEvents.forEach(e => allDayContainer.appendChild(createEventElement(e, 'default')));
                calendarGrid.appendChild(allDayContainer);
            }

            const dayContainer = document.createElement('div'); dayContainer.className = 'day-view-container';
            const labelsCol = document.createElement('div'); labelsCol.className = 'day-view-time-labels';
            const gridCol = document.createElement('div'); gridCol.className = 'day-view-grid-absolute';

            gridCol.innerHTML = `<div class="day-view-click-area" onclick="openEditEventModal(null, new Date('${date.toISOString()}').setHours(Math.floor(event.offsetY / 60) + 7, 0, 0, 0))"></div>`;
            for (let hour = 7; hour <= 18; hour++) { labelsCol.innerHTML += `<div class="day-view-time-label">${hour.toString().padStart(2, '0')}:00</div>`; }

            dayEvents.filter(e => !e.allDay).forEach(e => {
                const el = createEventElement(e, 'day-timed');
                let eStart = new Date(e.startTime), eEnd = new Date(e.endTime);
                const viewStart = new Date(date); viewStart.setHours(7, 0, 0, 0);
                if (eStart < viewStart) eStart = viewStart;
                if (eStart >= eEnd) return;
                el.style.top = `${(eStart.getHours() - 7) * 60 + eStart.getMinutes()}px`;
                el.style.height = `${(eEnd.getTime() - eStart.getTime()) / 60000}px`;
                gridCol.appendChild(el);
            });
            dayContainer.append(labelsCol, gridCol);
            calendarGrid.appendChild(dayContainer);

            const popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]');
            [...popoverTriggerList].map(popoverTriggerEl => new bootstrap.Popover(popoverTriggerEl));
        }

        async function fetchEvents(s, e) {
            try {
                // ATJAUNOTS: API tagad pats zina, kurš ir lietotājs, mēs vairs netiek sūtīts ?user=
                const res = await fetch(`/api/v1/calendar/events?start=${new Date(s.setHours(0,0,0,0)).toISOString()}&end=${new Date(e.setHours(23,59,59,999)).toISOString()}&_=${new Date().getTime()}`);
                if (res.ok) events = await res.json();
            } catch (err) { console.error(err); }
        }

        document.getElementById('prevMonthBtn').onclick = () => { if (currentView === 'day') currentDate.setDate(currentDate.getDate() - 1); else if (currentView === 'week') currentDate.setDate(currentDate.getDate() - 7); else currentDate.setMonth(currentDate.getMonth() - 1); renderCalendar(); };
        document.getElementById('nextMonthBtn').onclick = () => { if (currentView === 'day') currentDate.setDate(currentDate.getDate() + 1); else if (currentView === 'week') currentDate.setDate(currentDate.getDate() + 7); else currentDate.setMonth(currentDate.getMonth() + 1); renderCalendar(); };
        document.getElementById('todayBtn').onclick = () => { currentDate = new Date(); renderCalendar(); };

        window.openEditEventModal = function(existingEvent = null, date = null) {
            document.getElementById('eventForm').reset();
            document.getElementById('eventAlert').classList.add('d-none');

            inviteesSelector.clear();

            // Pārbauda, vai pašreizējais lietotājs ir notikuma autors
            const isCreator = !existingEvent || existingEvent.createdBy === currentUser;

            // Bloķē laukus, ja lietotājs nav autors
            document.getElementById('eventTitle').disabled = !isCreator;
            document.getElementById('eventCategory').disabled = !isCreator;
            document.getElementById('eventAllDay').disabled = !isCreator;
            document.getElementById('eventDesc').disabled = !isCreator;
            document.getElementById('saveEventBtn').style.display = isCreator ? 'inline-block' : 'none';

            if (existingEvent) {
                document.getElementById('eventModalTitle').innerText = isCreator ? 'Rediģēt Notikumu' : 'Skatīt Notikumu';
                document.getElementById('eventId').value = existingEvent.id;
                document.getElementById('eventTitle').value = existingEvent.title;
                document.getElementById('eventCategory').value = existingEvent.categoryId;
                document.getElementById('eventAllDay').checked = existingEvent.allDay;

                if (existingEvent.invitedPersons) {
                    inviteesSelector.setValues(existingEvent.invitedPersons);
                }

                initEventFlatpickr(!existingEvent.allDay); fpStart.setDate(existingEvent.startTime); fpEnd.setDate(existingEvent.endTime);
            } else {
                document.getElementById('eventModalTitle').innerText = 'Jauns Notikums';
                document.getElementById('eventId').value = '';
                document.getElementById('eventAllDay').checked = false;
                initEventFlatpickr(true);
                const startDate = new Date(date); if (startDate.getHours() === 0 && currentView !== 'day') startDate.setHours(9, 0, 0, 0);
                const endDate = new Date(startDate); endDate.setHours(startDate.getHours() + 1, 0, 0, 0);
                fpStart.setDate(startDate); fpEnd.setDate(endDate);
            }

            // Flatpickr datumu lauku bloķēšana (Flatpickr izveido slēpto lauku, bet rāda 'altInput' blakus)
            const startInput = document.getElementById('eventStart').nextElementSibling;
            if (startInput) startInput.disabled = !isCreator;
            const endInput = document.getElementById('eventEnd').nextElementSibling;
            if (endInput) endInput.disabled = !isCreator;

            // Iespējo/Atspējo uzaicināto personu izvēlni izmantojot jauno klases metodi
            inviteesSelector.setDisabled(!isCreator);

            eventModal.show();
        };

        document.getElementById('saveEventBtn').onclick = async () => {
            const selectedStart = fpStart.selectedDates[0];
            const selectedEnd = fpEnd.selectedDates[0];

            if (selectedStart && selectedEnd && (selectedEnd.getTime() < selectedStart.getTime())) {
                const alertBox = document.getElementById('eventAlert');
                alertBox.innerText = "Kļūda: Beigu datums un laiks nevar būt agrāks par sākuma datumu!";
                alertBox.classList.remove('d-none');
                return;
            }

            let sVal = document.getElementById('eventStart').value;
            let eVal = document.getElementById('eventEnd').value;
            if (sVal && sVal.length <= 10) sVal += "T00:00:00";
            if (eVal && eVal.length <= 10) eVal += "T00:00:00";

            const payload = {
                title: document.getElementById('eventTitle').value,
                categoryId: document.getElementById('eventCategory').value,
                startTime: sVal,
                endTime: eVal,
                allDay: document.getElementById('eventAllDay').checked,
                description: document.getElementById('eventDesc').value,
                createdBy: currentUser,
                invitedPersons: inviteesSelector.getValues()
            };

            const eventId = document.getElementById('eventId').value;
            try {
                const res = await fetch(eventId ? `/api/v1/calendar/events/${eventId}` : '/api/v1/calendar/events', {
                    method: eventId ? 'PUT' : 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                // LABOJUMS: Pareizi noparsējam kļūdas JSON, lai nerādītu "jēlu" kodu lietotājam
                if (!res.ok) {
                    const errData = await res.json();
                    throw new Error(errData.message || "Neizdevās saglabāt notikumu.");
                }

                eventModal.hide();
                renderCalendar();
            } catch (err) {
                document.getElementById('eventAlert').innerText = err.message;
                document.getElementById('eventAlert').classList.remove('d-none');
            }
        };

        renderCalendar();
    }

    // ==========================================
    // UZDEVUMU LOĢIKA
    // ==========================================
    if(document.getElementById('taskStartDate')) {
        flatpickr("#taskStartDate", { locale: "lv", altInput: true, altFormat: "d.m.Y", dateFormat: "Y-m-d", minDate: "today" });
        flatpickr("#taskDueDate", { locale: "lv", altInput: true, altFormat: "d.m.Y", dateFormat: "Y-m-d", minDate: "today" });
        const editStartPicker = flatpickr("#editTaskStartDate", { locale: "lv", altInput: true, altFormat: "d.m.Y", dateFormat: "Y-m-d" });
        const editDuePicker = flatpickr("#editTaskDueDate", { locale: "lv", altInput: true, altFormat: "d.m.Y", dateFormat: "Y-m-d" });

        const taskTypeSelect = document.getElementById('taskType');
        const mainAssigneeContainer = document.getElementById('mainAssigneeContainer');
        const subtasksContainer = document.getElementById('subtasksContainer');
        const addSubtaskBtn = document.getElementById('addSubtaskBtn');
        let subtaskCount = 0;

        if(taskTypeSelect) {
            taskTypeSelect.addEventListener('change', function() {
                subtasksContainer.innerHTML = ''; subtaskCount = 0; addSubtaskBtn.style.display = 'inline-block';
                if (this.value === 'REGULAR') { mainAssigneeContainer.style.display = 'flex'; document.getElementById('taskAssignee').required = true; }
                else { mainAssigneeContainer.style.display = 'none'; document.getElementById('taskAssignee').required = false; addSubtaskBtn.click(); addSubtaskBtn.click(); }
            });
        }

        if(addSubtaskBtn) {
            addSubtaskBtn.addEventListener('click', function() {
                const isComplex = taskTypeSelect.value !== 'REGULAR';
                if (subtaskCount >= (isComplex ? 3 : 10)) return alert(`Maksimālais apakšuzdevumu skaits sasniegts.`);
                const row = document.createElement('div'); row.className = 'card mb-3 border-info subtask-row';
                if (isComplex) {
                    row.innerHTML = `<div class="card-body bg-light"><div class="d-flex justify-content-between mb-2"><h6 class="fw-bold text-info">Solis ${subtaskCount + 1}</h6><button class="btn btn-sm btn-outline-danger remove-subtask-btn" type="button">&times;</button></div><div class="row mb-2"><div class="col-md-6"><input type="text" class="form-control form-control-sm subtask-assignee" placeholder="Atbildīgais" required></div><div class="col-md-6"><input type="date" class="form-control form-control-sm subtask-date" required></div></div><textarea class="form-control form-control-sm subtask-desc" placeholder="Apraksts..." required></textarea></div>`;
                } else {
                    row.innerHTML = `<div class="input-group"><input type="text" class="form-control subtask-title" placeholder="Apakšuzdevums" required><button class="btn btn-outline-danger remove-subtask-btn" type="button">Dzēst</button></div>`;
                }
                subtasksContainer.appendChild(row); subtaskCount++;
                if (isComplex) flatpickr(row.querySelector('.subtask-date'), { locale: "lv", altInput: true, altFormat: "d.m.Y", dateFormat: "Y-m-d", minDate: "today" });
                row.querySelector('.remove-subtask-btn').onclick = () => { row.remove(); subtaskCount--; addSubtaskBtn.style.display = 'inline-block'; };
            });
        }

        if(document.getElementById('saveTaskBtn')) {
            document.getElementById('saveTaskBtn').onclick = async () => {
                const formatToISO = (dateStr) => { if (!dateStr) return null; if (dateStr.includes('-')) return dateStr; const p = dateStr.split('.'); return `${p[2]}-${p[1]}-${p[0]}`; };
                const payload = {
                    taskType: taskTypeSelect.value,
                    title: document.getElementById('taskTitle').value,
                    startDate: formatToISO(document.getElementById('taskStartDate').value),
                    dueDate: formatToISO(document.getElementById('taskDueDate').value),
                    assignee: taskTypeSelect.value !== 'REGULAR' ? currentUser : assigneeSelector.getValues(),
                    followers: followersSelector.getValues(),
                    createdBy: currentUser,
                    description: document.getElementById('taskDescription').value,
                    priority: "NORMAL", status: "Nav sākts", subTasks: []
                };
                document.querySelectorAll('.subtask-row').forEach(row => {
                    if (taskTypeSelect.value !== 'REGULAR') payload.subTasks.push({ title: "Apakšuzdevums", assignee: row.querySelector('.subtask-assignee').value, dueDate: formatToISO(row.querySelector('.subtask-date').value), description: row.querySelector('.subtask-desc').value });
                    else { const t = row.querySelector('.subtask-title').value; if (t) payload.subTasks.push({ title: t }); }
                });
                try {
                    const res = await fetch('/api/v1/tasks', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
                    if (!res.ok) throw new Error(await res.text()); window.location.reload();
                } catch (err) { document.getElementById('createTaskAlert').innerText = err.message; document.getElementById('createTaskAlert').classList.remove('d-none'); }
            };
        }

        const editTaskModalEl = document.getElementById('editTaskModal');
        if(editTaskModalEl) {
            const editModal = new bootstrap.Modal(editTaskModalEl);
            document.querySelectorAll('.task-title-link').forEach(link => {
                link.onclick = async function(e) {
                    e.preventDefault();
                    try {
                        const task = await (await fetch(`/api/v1/tasks/${this.getAttribute('data-id')}`)).json();
                        document.getElementById('editTaskId').value = task.id; document.getElementById('editTaskTitle').value = task.title; document.getElementById('editTaskDescription').value = task.description;
                        editStartPicker.setDate(task.startDate); editDuePicker.setDate(task.dueDate);
                        const isCreator = (task.assignee === currentUser);
                        ['editTaskTitle', 'editTaskDescription', 'editTaskStartDate', 'editTaskDueDate'].forEach(id => { const el = document.getElementById(id); if (el.classList.contains('flatpickr-input')) el.nextElementSibling.disabled = !isCreator; else el.disabled = !isCreator; });

                        const subContainer = document.getElementById('editSubtasksContainer'); subContainer.innerHTML = '';
                        let hasEditable = isCreator;
                        (task.subTasks || []).forEach(st => {
                            const isEditable = (st.assignee === currentUser) && st.active; if (isEditable) hasEditable = true;
                            subContainer.insertAdjacentHTML('beforeend', `<div class="card mb-2 border-secondary subtask-card" data-id="${st.id}" data-order="${st.orderIndex}"><div class="card-body p-2 bg-light"><div class="d-flex justify-content-between mb-1"><span class="fw-bold small">${st.orderIndex}. ${st.title} (${st.assignee})</span><span class="badge ${st.active ? 'bg-primary' : 'bg-secondary'}">${st.status}</span></div><textarea class="form-control form-control-sm mb-2" id="stDesc_${st.id}" ${isEditable ? '' : 'disabled'}>${st.description || ''}</textarea>${isEditable ? `<select class="form-select form-select-sm status-changer-modal"><option value="${st.status}" hidden>${st.status}</option><option value="Notiek izpilde">Notiek izpilde</option><option value="Atgriezts labošanai">Atgriezts labošanai</option><option value="Pabeigts">Pabeigts</option></select>` : ''}</div></div>`);
                        });
                        document.getElementById('updateTaskBtn').style.display = hasEditable ? 'inline-block' : 'none'; editModal.show();
                    } catch (err) { console.error(err); }
                };
            });

            document.getElementById('updateTaskBtn').onclick = async () => {
                const reqs = [];
                if (!document.getElementById('editTaskTitle').disabled) reqs.push({ url: `/api/v1/tasks/${document.getElementById('editTaskId').value}`, body: { title: document.getElementById('editTaskTitle').value, description: document.getElementById('editTaskDescription').value, startDate: document.getElementById('editTaskStartDate').value, dueDate: document.getElementById('editTaskDueDate').value } });
                document.querySelectorAll('.subtask-card').forEach(card => {
                    const sel = card.querySelector('.status-changer-modal'), txt = document.getElementById(`stDesc_${card.getAttribute('data-id')}`);
                    if ((sel && !sel.disabled) || (txt && !txt.disabled)) reqs.push({ url: `/api/v1/tasks/subtasks/${card.getAttribute('data-id')}`, body: { status: sel ? sel.value : undefined, description: txt ? txt.value : undefined } });
                });
                for (const req of reqs) { const res = await fetch(req.url, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(req.body) }); if (!res.ok) return alert(await res.text()); }
                window.location.reload();
            };
        }

        // ==========================================
        // POPOVER (TOAST) PAZIŅOJUMI
        // ==========================================
        function fetchNotifications() {
            // ATJAUNOTS: Arī šeit API pats nolasa lietotāju
            fetch(`/api/v1/tasks/notifications`)
                .then(res => res.json()).then(notifications => {
                const container = document.getElementById('toastContainer');
                if(!container) return;
                notifications.forEach(notif => {
                    if (!document.getElementById(`toast-${notif.id}`)) {
                        const toastEl = document.createElement('div');
                        toastEl.className = 'toast border-primary shadow'; toastEl.id = `toast-${notif.id}`; toastEl.setAttribute('role', 'alert'); toastEl.setAttribute('aria-live', 'assertive'); toastEl.setAttribute('aria-atomic', 'true');
                        toastEl.innerHTML = `<div class="toast-header bg-primary text-white"><strong class="me-auto">DVS Paziņojums</strong><button type="button" class="btn-close btn-close-white close-toast" data-id="${notif.id}"></button></div><div class="toast-body"><div class="fw-bold mb-3">${notif.message}</div><div class="text-end"><button type="button" class="btn btn-sm btn-primary read-toast-btn" data-id="${notif.id}">Esmu iepazinies</button></div></div>`;
                        container.appendChild(toastEl);
                        const bsToast = new bootstrap.Toast(toastEl, { autohide: false }); bsToast.show();
                        const markAsRead = function() { fetch(`/api/v1/tasks/notifications/${this.getAttribute('data-id')}/read`, { method: 'POST' }); bsToast.hide(); setTimeout(() => toastEl.remove(), 500); };
                        toastEl.querySelector('.close-toast').onclick = markAsRead; toastEl.querySelector('.read-toast-btn').onclick = markAsRead;
                    }
                });
            });
        }
        setInterval(fetchNotifications, 300000);
        fetchNotifications();
    }
});