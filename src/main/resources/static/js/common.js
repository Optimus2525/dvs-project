// Nolasa CSRF tokenu no pārlūka sīkdatnēm (Cookies)
function getCsrfToken() {
    const match = document.cookie.match(new RegExp('(^| )XSRF-TOKEN=([^;]+)'));
    return match ? match[2] : null;
}

// Globāli pārraksta (Override) native fetch API, lai automātiski pievienotu CSRF hederi visiem POST/PUT/DELETE pieprasījumiem
const originalFetch = window.fetch;
window.fetch = function() {
    let [resource, config] = arguments;
    if (config && (config.method === 'POST' || config.method === 'PUT' || config.method === 'DELETE')) {
        config.headers = config.headers || {};
        const csrfToken = getCsrfToken();
        if (csrfToken) {
            config.headers['X-XSRF-TOKEN'] = csrfToken;
        }
    }
    return originalFetch(resource, config);
};

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


// ==========================================
// UNIVERSĀLS ENTRA ID LIETOTĀJU MEKLĒTĀJS
// ==========================================
window.EntraUserSelector = class {
    constructor(options) {
        this.input = document.getElementById(options.inputId);
        if (!this.input) return;

        this.datalist = document.getElementById(options.datalistId);
        this.pillsContainer = options.pillsContainerId ? document.getElementById(options.pillsContainerId) : null;
        this.alertBox = options.alertBoxId ? document.getElementById(options.alertBoxId) : null;
        this.isMultiple = options.isMultiple || false;
        this.onUserAdded = options.onUserAdded || null;

        // JAUNUMS: Ļauj norādīt API galapunktu (pēc noklusējuma lokālā bāze)
        this.searchUrl = options.searchUrl || '/api/v1/tasks/users/search';

        this.selectedUsers = [];
        this.initListeners();
    }

    async resolveApi(query) {
        if (!query || query.trim().length < 2) return null;
        try {
            // ATJAUNOTS: Izmanto this.searchUrl
            const res = await fetch(`${this.searchUrl}?query=${encodeURIComponent(query.trim())}`);
            if (res.ok) {
                const users = await res.json();
                if (users.length > 0) return users[0];
            }
        } catch (e) { console.error("Kļūda API:", e); }
        return null;
    }

    initListeners() {
        this.input.addEventListener('input', () => {
            const val = this.input.value.trim();
            const options = Array.from(this.datalist.options).map(o => o.value);

            if (options.includes(val)) {
                this.processSelection(val);
                return;
            }

            if (val.length < 2) return;
            // ATJAUNOTS: Izmanto this.searchUrl
            fetch(`${this.searchUrl}?query=${encodeURIComponent(val)}`)
                .then(res => res.json()).then(users => {
                this.datalist.innerHTML = '';
                users.forEach(u => this.datalist.appendChild(Object.assign(document.createElement('option'), {value: u})));
            });
        });

        this.input.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                this.processSelection(this.input.value);
            }
        });

        if (!this.isMultiple) {
            this.input.addEventListener('change', () => {
                this.processSelection(this.input.value);
            });
        }
    }

    async processSelection(val) {
        val = val.trim();
        if (!val) return;

        const options = Array.from(this.datalist.options).map(o => o.value);
        let resolvedName = val;

        if (val.length >= 2 && !options.includes(val)) {
            this.input.disabled = true;
            const origPlaceholder = this.input.placeholder;
            this.input.value = 'Meklē...';
            resolvedName = await this.resolveApi(val);
            this.input.disabled = false;
            this.input.placeholder = origPlaceholder;
        }

        if (resolvedName) {
            if (this.isMultiple) {
                if (!this.selectedUsers.includes(resolvedName)) {
                    this.selectedUsers.push(resolvedName);
                    await this.renderPill(resolvedName);
                }
                this.input.value = '';
                this.input.focus();
            } else {
                this.input.value = resolvedName;
                this.selectedUsers = [resolvedName];
            }
        } else {
            this.showError(`Lietotājs "${val}" nav atrasts sistēmā!`);
            if (this.isMultiple) {
                this.input.value = '';
                this.input.focus();
            } else {
                this.input.value = '';
            }
        }
    }

    async renderPill(name) {
        const badge = document.createElement('span');
        badge.className = 'badge border border-secondary bg-transparent text-dark fw-normal fs-6 rounded-pill me-2 mb-2 px-3 py-2 shadow-sm';
        // Pievienota klase 'remove-pill-btn', lai mēs varētu to atrast un paslēpt
        badge.innerHTML = `${name} <span class="ms-2 text-muted fw-bold remove-pill-btn" style="cursor:pointer;" title="Noņemt">&times;</span>`;

        const closeBtn = badge.querySelector('span.ms-2');
        closeBtn.onmouseover = () => closeBtn.classList.replace('text-muted', 'text-danger');
        closeBtn.onmouseout = () => closeBtn.classList.replace('text-danger', 'text-muted');
        closeBtn.onclick = () => {
            this.selectedUsers = this.selectedUsers.filter(f => f !== name);
            badge.remove();
        };

        this.pillsContainer.appendChild(badge);

        if (this.onUserAdded) {
            await this.onUserAdded(name, badge);
        }
    }

    showError(msg) {
        if (this.alertBox) {
            this.alertBox.innerText = msg;
            this.alertBox.classList.remove('d-none');
            setTimeout(() => this.alertBox.classList.add('d-none'), 4000);
        }
    }

    getValues() {
        return this.isMultiple ? this.selectedUsers : (this.selectedUsers[0] || this.input.value);
    }

    clear() {
        this.selectedUsers = [];
        if (this.isMultiple && this.pillsContainer) this.pillsContainer.innerHTML = '';
        if (!this.isMultiple && this.input) this.input.value = '';
    }

    setValues(users) {
        this.clear();
        if(!users) return;
        if (this.isMultiple && Array.isArray(users)) {
            users.forEach(u => {
                this.selectedUsers.push(u);
                this.renderPill(u);
            });
        } else if (!this.isMultiple) {
            this.input.value = users;
            this.selectedUsers = [users];
        }
    }


    // JAUNA METODE: Iespējo/Atspējo komponenti (Read-Only režīms)
    setDisabled(isDisabled) {
        this.input.disabled = isDisabled;
        // Paslēpj ievades lauku, ja tas ir atspējots
        this.input.style.display = isDisabled ? 'none' : '';

        // Paslēpj visus dzēšanas krustiņus
        if (this.pillsContainer) {
            const crosses = this.pillsContainer.querySelectorAll('.remove-pill-btn');
            crosses.forEach(c => c.style.display = isDisabled ? 'none' : 'inline');
        }
    }


}