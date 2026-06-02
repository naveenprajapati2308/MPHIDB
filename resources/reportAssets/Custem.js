(function () {

    function buildNavbar() {

        var nb = document.querySelector('.navbar');

        if (!nb || document.getElementById('ext-left'))
            return;

        Array.from(nb.children).forEach(function (el) {

            el.style.display = 'none';

        });

        // ================= LEFT =================

        var left = document.createElement('div');

        left.id = 'ext-left';

        // ================= LOGO =================

        var logo = document.createElement('div');

        logo.id = 'ext-logo-box';

        var img = document.createElement('img');

        img.id = 'ext-logo-img';

        img.src = '__LOGO__';

        logo.appendChild(img);

        // ================= DIVIDER =================

        var divider = document.createElement('div');

        divider.id = 'ext-divider';

        // ================= TITLE =================

        var titleWrap = document.createElement('div');

        titleWrap.id = 'ext-title-wrap';

        var title = document.createElement('div');

        title.id = 'ext-title';

        title.innerText = 'Automation Test Report';

        var subtitle = document.createElement('div');

        subtitle.id = 'ext-subtitle';

        subtitle.innerText = 'MPHIDB Test Result';

        titleWrap.appendChild(title);

        titleWrap.appendChild(subtitle);

        left.appendChild(logo);

        left.appendChild(divider);

        left.appendChild(titleWrap);

        // ================= SEARCH =================

        var searchWrap = document.createElement('div');

        searchWrap.id = 'ext-search-wrap';

        var search = document.createElement('input');

        search.id = 'ext-search';

        search.placeholder = 'Search test cases...';

        search.addEventListener('input', function () {

            var q = this.value.toLowerCase();

            document.querySelectorAll('.test-item').forEach(function (item) {

                var txt = item.innerText.toLowerCase();

                item.style.display = txt.includes(q) ? '' : 'none';

            });

        });

        searchWrap.appendChild(search);

        // ================= RIGHT BADGES =================

        var right = document.createElement('div');

        right.id = 'ext-right';

        var badges = [

            '📅 __EXEC_DATE__',

            '🏢 QA',

            '👤 __TESTER__',

            '🔗 __VERSION__'

        ];

        badges.forEach(function (txt) {

            var b = document.createElement('div');

            b.className = 'top-badge';

            b.innerHTML = txt;

            right.appendChild(b);

        });

        // ================= APPEND =================

        nb.appendChild(left);

        nb.appendChild(searchWrap);

        nb.appendChild(right);

    }

    // ================= DASHBOARD =================

    function createDashboard() {

        if (document.getElementById('ext-dashboard'))
            return;

        var container = document.querySelector('.container-fluid');

        if (!container)
            return;

        // ================= DYNAMIC DATA =================

        var totalTests =
            document.querySelectorAll('.test-item').length;

        var passedTests =
            document.querySelectorAll(".test-item[status='pass']").length;

        var failedTests =
            document.querySelectorAll(".test-item[status='fail']").length;

        var skippedTests =
            document.querySelectorAll(".test-item[status='skip']").length;

        // ================= BAR HEIGHT =================

        var maxBarHeight = 160;

        var total =
            passedTests + failedTests + skippedTests;

        var passHeight =
            total > 0
                ? (passedTests / total) * maxBarHeight
                : 0;

        var failHeight =
            total > 0
                ? (failedTests / total) * maxBarHeight
                : 0;

        var skipHeight =
            total > 0
                ? (skippedTests / total) * maxBarHeight
                : 0;

        // ================= DASHBOARD =================

        var dash = document.createElement('div');

        dash.id = 'ext-dashboard';

        dash.style.cssText =
            'display:grid;grid-template-columns:320px 1fr;gap:16px;margin-bottom:18px;';

        // ================= SUMMARY CARD =================

        var card = document.createElement('div');

        card.className = 'card';

        card.style.padding = '18px';

        card.innerHTML =

            '<h3 style="color:white;margin-bottom:14px;font-size:18px;">Test Summary</h3>' +

            '<div style="display:flex;justify-content:space-between;margin-bottom:10px;color:#e2e8f0;"><span>Total Tests</span><b>' + totalTests + '</b></div>' +

            '<div style="display:flex;justify-content:space-between;margin-bottom:10px;color:#00c853;"><span>Passed</span><b>' + passedTests + '</b></div>' +

            '<div style="display:flex;justify-content:space-between;margin-bottom:10px;color:#ff5252;"><span>Failed</span><b>' + failedTests + '</b></div>' +

            '<div style="display:flex;justify-content:space-between;color:#ffab00;"><span>Skipped</span><b>' + skippedTests + '</b></div>';

        // ================= GRAPH =================

        var graph = document.createElement('div');

        graph.className = 'chart-container';

        graph.style.padding = '18px';

        graph.innerHTML =

            '<h3 style="color:white;margin-bottom:20px;font-size:18px;">Execution Overview</h3>' +

            '<div style="height:220px;display:flex;align-items:flex-end;justify-content:space-around;">' +

            '<div><div style="width:60px;height:' + passHeight + 'px;background:#00c853;border-radius:10px 10px 0 0;"></div><div style="text-align:center;color:white;margin-top:8px;">Passed</div></div>' +

            '<div><div style="width:60px;height:' + failHeight + 'px;background:#ff5252;border-radius:10px 10px 0 0;"></div><div style="text-align:center;color:white;margin-top:8px;">Failed</div></div>' +

            '<div><div style="width:60px;height:' + skipHeight + 'px;background:#ffab00;border-radius:10px 10px 0 0;"></div><div style="text-align:center;color:white;margin-top:8px;">Skipped</div></div>' +

            '</div>';

        dash.appendChild(card);

        dash.appendChild(graph);

        container.prepend(dash);

    }

    // ================= LOAD =================

    buildNavbar();

    createDashboard();

    document.addEventListener('DOMContentLoaded', function () {

        buildNavbar();

        createDashboard();

    });

    window.addEventListener('load', function () {

        setTimeout(buildNavbar, 300);

        setTimeout(createDashboard, 300);

    });

	
	
	
	overviewCard.innerHTML = `
	<div class="execution-overview-header">
	    <h2>Execution Overview</h2>

	    <select id="moduleFilter">
	        <option value="all">All Modules</option>
	        ${Object.keys(moduleData).map(module =>
	            `<option value="${module}">${module}</option>`
	        ).join("")}
	    </select>
	</div>

	<div class="overview-chart">

	    <div class="overview-item total">
	        <div class="overview-count">${data.total}</div>
	        <div class="overview-bar total-bar"
	             style="height:${Math.max(data.total * 12, 40)}px"></div>
	        <div class="overview-label">Total</div>
	    </div>

	    <div class="overview-item passed">
	        <div class="overview-count">${data.pass}</div>
	        <div class="overview-bar passed-bar"
	             style="height:${Math.max(data.pass * 12, 40)}px"></div>
	        <div class="overview-label">Passed</div>
	    </div>

	    <div class="overview-item failed">
	        <div class="overview-count">${data.fail}</div>
	        <div class="overview-bar failed-bar"
	             style="height:${Math.max(data.fail * 12, 40)}px"></div>
	        <div class="overview-label">Failed</div>
	    </div>

	    <div class="overview-item skipped">
	        <div class="overview-count">${data.skip}</div>
	        <div class="overview-bar skipped-bar"
	             style="height:${Math.max(data.skip * 12, 40)}px"></div>
	        <div class="overview-label">Skipped</div>
	    </div>

	</div>
	`;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
})();