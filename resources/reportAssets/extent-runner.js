(function() {

    var runnerBaseUrl = window.location.protocol.indexOf('http') === 0
        ? window.location.origin
        : 'http://localhost:9090';

    var suiteMap = {
        'Land Module Tests': 'land',
        'land Management Tests': 'land',
        'Architect Empanelment Tests': 'architect',
        'Architect Tests': 'architect'
    };

    function getSuiteKey(moduleName) {
        if (suiteMap[moduleName])
            return suiteMap[moduleName];

        var normalized = String(moduleName || '').toLowerCase();

        if (normalized.indexOf('land') >= 0)
            return 'land';

        if (normalized.indexOf('architect') >= 0)
            return 'architect';

        return 'all';
    }

    function runSuite(suiteKey, label) {
        if (!window.confirm('Run ' + label + ' now?'))
            return;

        fetch(runnerBaseUrl + '/run?suite=' + encodeURIComponent(suiteKey), {
            method: 'POST'
        }).then(function(response) {
            if (response.ok || response.status === 202)
                return response.text();

            return response.text().then(function(text) {
                throw new Error(text || 'Runner request failed');
            });
        }).then(function(message) {
            alert(message + '\n\nOpen runner status: ' + runnerBaseUrl + '/status');
        }).catch(function(error) {
            alert('Unable to start suite.\n\nMake sure the runner backend is running at:\n'
                + runnerBaseUrl + '\n\n' + error.message);
        });
    }

    function buildButton(label, suiteKey, extraClass) {
        var button = document.createElement('button');
        button.type = 'button';
        button.className = 'ext-run-btn ' + (extraClass || '');
        button.innerText = label;
        button.addEventListener('click', function() {
            runSuite(suiteKey, label);
        });
        return button;
    }

    function addRunnerButtons() {
        var dashboard = document.getElementById('ext-dashboard');

        if (!dashboard || document.getElementById('ext-runner-actions'))
            return;

        var moduleCard = null;
        var cards = dashboard.querySelectorAll('.card');

        cards.forEach(function(card) {
            if (card.innerText && card.innerText.indexOf('Module Analytics') >= 0)
                moduleCard = card;
        });

        if (!moduleCard)
            return;

        var title = moduleCard.querySelector('.section-title');
        var actions = document.createElement('div');
        actions.id = 'ext-runner-actions';
        actions.appendChild(buildButton('Run All Modules', 'all', 'primary'));

        if (title) {
            var header = document.createElement('div');
            header.className = 'ext-runner-header';
            title.parentNode.insertBefore(header, title);
            header.appendChild(title);
            header.appendChild(actions);
        }

        var table = moduleCard.querySelector('table.ext-table');

        if (!table)
            return;

        var headRow = table.querySelector('thead tr');

        if (headRow && !headRow.querySelector('.ext-runner-th')) {
            var actionHead = document.createElement('th');
            actionHead.className = 'ext-runner-th';
            actionHead.innerText = 'Run';
            headRow.appendChild(actionHead);
        }

        table.querySelectorAll('tbody tr').forEach(function(row) {
            if (row.querySelector('.ext-runner-td'))
                return;

            var moduleName = row.children.length ? row.children[0].innerText.trim() : '';
            var suiteKey = getSuiteKey(moduleName);
            var actionCell = document.createElement('td');
            actionCell.className = 'ext-runner-td';
            actionCell.appendChild(buildButton('Run Module', suiteKey, 'small'));
            row.appendChild(actionCell);
        });
    }

    document.addEventListener('DOMContentLoaded', function() {
        setTimeout(addRunnerButtons, 700);
        setTimeout(addRunnerButtons, 1500);
    });

    window.addEventListener('load', function() {
        setTimeout(addRunnerButtons, 700);
        setTimeout(addRunnerButtons, 1500);
    });

})();
