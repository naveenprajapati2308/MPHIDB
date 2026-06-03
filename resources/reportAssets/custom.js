(function() {

    // ================= NAVBAR =================

    function buildNavbar() {

        var nb = document.querySelector('.navbar');

        if (!nb || document.getElementById('ext-left'))
            return;

        Array.from(nb.children).forEach(function(el) {

            el.style.display = 'none';

        });

        // ================= LEFT =================

        var left = document.createElement('div');

        left.id = 'ext-left';

        var logo = document.createElement('div');

        logo.id = 'ext-logo-box';

        var img = document.createElement('img');

        img.id = 'ext-logo-img';

        img.src = '__LOGO__';

        logo.appendChild(img);

        var divider = document.createElement('div');

        divider.id = 'ext-divider';

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

        search.addEventListener('input', function() {

            var q = this.value.toLowerCase();

            document.querySelectorAll('.test-item').forEach(function(item) {

                var txt = item.innerText.toLowerCase();

                item.style.display = txt.includes(q) ? '' : 'none';

            });

        });

        searchWrap.appendChild(search);

        // ================= RIGHT =================

        var right = document.createElement('div');

        right.id = 'ext-right';

        var badges = [

            '📅 __EXEC_DATE__',

            '🏢 QA',

            '👤 __TESTER__',

            '🔗 __VERSION__'

        ];

        badges.forEach(function(txt) {

            var b = document.createElement('div');

            b.className = 'top-badge';

            b.innerHTML = txt;

            right.appendChild(b);

        });

        nb.appendChild(left);

        nb.appendChild(searchWrap);

        nb.appendChild(right);

    }

    // ================= DASHBOARD =================

    function createDashboard() {
        // ================= HIDE ORIGINAL TOP DASHBOARD =================

        var dashboardView = document.querySelector('.dashboard-view');

        if (dashboardView) {

            var originalRows = dashboardView.querySelectorAll('.row');

            if (originalRows.length > 0) {

                originalRows[0].style.display = 'none';

            }
        }

        if (document.getElementById('ext-dashboard'))
            return;

        var container = document.querySelector('.container-fluid');

        if (!container)
            return;

        // ================= COUNTS =================

        var totalTests =
            document.querySelectorAll('.test-item[status][tag]').length;

        var passedTests =
            document.querySelectorAll(".test-item[status='pass'][tag]").length;

        var failedTests =
            document.querySelectorAll(".test-item[status='fail'][tag]").length;

        var skippedTests =
            document.querySelectorAll(".test-item[status='skip'][tag]").length;

        var accuracy =
            totalTests > 0
                ? ((passedTests / totalTests) * 100).toFixed(2)
                : 0;

        // ================= MODULE DATA =================

        var moduleData = {};

        document.querySelectorAll('.test-item[status][tag]').forEach(function(test) {

            var module =
                test.getAttribute('tag') || 'Unknown';

            var status =
                test.getAttribute('status');

            if (!moduleData[module]) {

                moduleData[module] = {

                    total: 0,
                    pass: 0,
                    fail: 0,
                    skip: 0

                };
            }

            moduleData[module].total++;

            if (status === 'pass')
                moduleData[module].pass++;

            else if (status === 'fail')
                moduleData[module].fail++;

            else if (status === 'skip')
                moduleData[module].skip++;

        });

        // ================= MAIN WRAPPER =================

        var dash = document.createElement('div');

        dash.id = 'ext-dashboard';

        dash.style.cssText =
            'display:flex;flex-direction:column;gap:18px;margin-bottom:24px;';

        // ================= TOP ROW =================

        var topRow = document.createElement('div');

        topRow.style.cssText =
            'display:grid;grid-template-columns:320px 1fr 1fr 1fr 1fr;;gap:16px;';

        // ================= SUMMARY =================

        var summary = document.createElement('div');

        summary.className = 'card';

        summary.style.padding = '20px';

        summary.innerHTML =

            '<h3 style="color:white;margin-bottom:18px;">Test Summary</h3>' +

            '<div class="ext-stat-row"><span>Total Tests</span><b>' + totalTests + '</b></div>' +

            '<div class="ext-stat-row pass"><span>Passed</span><b>' + passedTests + '</b></div>' +

            '<div class="ext-stat-row fail"><span>Failed</span><b>' + failedTests + '</b></div>' +

            '<div class="ext-stat-row skip"><span>Skipped</span><b>' + skippedTests + '</b></div>';

        // ================= START =================

        var startCard = document.createElement('div');

        startCard.className = 'card';

        startCard.style.padding = '24px';

        startCard.innerHTML =

            '<div class="ext-mini-title">⏱ Execution Started</div>' +

            '<div class="ext-date">__EXEC_DATE__</div>' +

            '<div class="ext-time">__START_TIME__</div>';

        // ================= END =================

        var endCard = document.createElement('div');

        endCard.className = 'card';

        endCard.style.padding = '24px';

        endCard.innerHTML =

            '<div class="ext-mini-title">⌛ Execution Ended</div>' +

            '<div class="ext-date">__END_DATE__</div>' +

            '<div class="ext-time">__END_TIME__</div>';


        // ================exicution duration card =========
        var durationCard =
            document.createElement('div');

        durationCard.className = 'card';

        durationCard.innerHTML =
            '<div class="ext-mini-title">⏳ Duration</div>' +
            '<div class="ext-date">Total Runtime</div>' +
            '<div class="ext-time">__EXEC_DURATION__</div>' + '<div class="ext-time" > Hour</div>';

        // ================= ACCURACY =================

        var accuracyCard = document.createElement('div');

        accuracyCard.className = 'card';

        accuracyCard.style.padding = '24px';

        accuracyCard.innerHTML =

            '<div class="ext-mini-title">🎯 Total Accuracy</div>' +

            '<div class="accuracy-wrap">' +

            '<div>' +

            '<div class="accuracy-value">' + accuracy + '%</div>' +

            '<div class="accuracy-sub">(' + passedTests + '/' + totalTests + ' Tests Passed)</div>' +

            '</div>' +

            '<div class="circle-wrap">' +

            '<svg width="90" height="90">' +

            '<circle cx="45" cy="45" r="36" stroke="#1e293b" stroke-width="10" fill="none"></circle>' +

            '<circle cx="45" cy="45" r="36" stroke="#00c853" stroke-width="10" fill="none"' +

            'stroke-dasharray="' + (accuracy * 2.26) + ' 226" transform="rotate(-90 45 45)"></circle>' +

            '</svg>' +

            '<div class="circle-text">' + accuracy + '%</div>' +

            '</div>' +

            '</div>';

        topRow.appendChild(summary);

        topRow.appendChild(startCard);

        topRow.appendChild(endCard);

        topRow.appendChild(durationCard);

        topRow.appendChild(accuracyCard);

        // ================= MODULE ANALYTICS =================

        var moduleCard =
            document.createElement('div');

        moduleCard.className = 'card';

        moduleCard.style.cssText =
            'padding:18px;';

        var moduleHtml =

            '<h3 class="section-title">Module Analytics</h3>';

        moduleHtml +=

            '<table class="ext-table">' +

            '<thead>' +

            '<tr>' +

            '<th>Module</th>' +

            '<th>Total</th>' +

            '<th class="green">Passed</th>' +

            '<th class="red">Failed</th>' +

            '<th class="yellow">Skipped</th>' +

            '<th>Accuracy</th>' +

            '</tr>' +

            '</thead><tbody>';

        for (var module in moduleData) {

            var m = moduleData[module];

            var moduleAcc =
                m.total > 0
                    ? ((m.pass / m.total) * 100).toFixed(2)
                    : 0;

            moduleHtml +=

                '<tr>' +

                '<td>' + module + '</td>' +

                '<td>' + m.total + '</td>' +

                '<td class="green">' + m.pass + '</td>' +

                '<td class="red">' + m.fail + '</td>' +

                '<td class="yellow">' + m.skip + '</td>' +

                '<td>' +

                '<div class="progress-wrap">' +

                '<span>' + moduleAcc + '%</span>' +

                '<div class="progress-bar">' +

                '<div class="progress-fill" style="width:' + moduleAcc + '%;"></div>' +

                '</div>' +

                '</div>' +

                '</td>' +

                '</tr>';
        }

        moduleHtml += '</tbody></table>';



        moduleCard.innerHTML = moduleHtml;




        dash.appendChild(topRow);

        dash.appendChild(moduleCard);

        // ================= DASHBOARD VIEW =================

        var dashboardView =
            document.querySelector('.dashboard-view');

        // ================= INSERT =================

        var dashboardCards =
            dashboardView.querySelector('.row');

        if (dashboardCards) {

            dashboardCards.after(dash);

        } else {

            dashboardView.prepend(dash);

        }
        // ================= REPLACE TAGS WITH EXECUTION OVERVIEW =================

        setTimeout(function() {


            var tagCards = document.querySelectorAll('.card, .panel');

            var tagsCard = null;

            tagCards.forEach(function(card) {

                if (
                    card.querySelector("table") &&
                    card.innerText.toLowerCase().includes("passed %")
                ) {

                    tagsCard = card;
                }
            });

            if (!tagsCard) return;

            // ================= COUNTS =================

            var totalTests =
                document.querySelectorAll('.test-item[status][tag]').length;

            var passedTests =
                document.querySelectorAll(".test-item[status='pass'][tag]").length;

            var failedTests =
                document.querySelectorAll(".test-item[status='fail'][tag]").length;

            var skippedTests =
                document.querySelectorAll(".test-item[status='skip'][tag]").length;

            // ================= MODULE DATA =================

            var moduleData = {};

            document.querySelectorAll('.test-item[status][tag]').forEach(function(test) {

                var module =
                    test.getAttribute('tag') || 'Unknown';

                var status =
                    test.getAttribute('status');

                if (!moduleData[module]) {

                    moduleData[module] = {

                        total: 0,
                        pass: 0,
                        fail: 0,
                        skip: 0
                    };
                }

                moduleData[module].total++;

                if (status === 'pass')
                    moduleData[module].pass++;

                else if (status === 'fail')
                    moduleData[module].fail++;

                else if (status === 'skip')
                    moduleData[module].skip++;
            });

            // ================= DROPDOWN =================

            var options =
                '<option value="ALL">All Modules</option>';

            for (var module in moduleData) {

                options +=
                    '<option value="' + module + '">' +
                    module +
                    '</option>';
            }

            // ================= NEW HTML =================

            tagsCard.innerHTML =

                '<div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:25px;">' +

                '<h3 class="section-title" style="margin:0;">Execution Overview</h3>' +

                '<select id="overviewFilter" class="ext-select">' +

                options +

                '</select>' +

                '</div>' +

                '<div class="overview-wrap">' +

                '<div class="overview-box">' +

                '<div class="overview-bar blue-bg" id="ovTotalBar" style="height:220px;"></div>' +

                '<div class="overview-count blue" id="ovTotal">' + totalTests + '</div>' +

                '<div class="overview-label">Total</div>' +

                '</div>' +



                '<div class="overview-box">' +

                '<div class="overview-bar green-bg" id="ovPassBar" style="height:' + ((passedTests / totalTests) * 220) + 'px;"></div>' +
                '<div class="overview-count green" id="ovPass">' + passedTests + '</div>' +
                '<div class="overview-label">Passed</div>' +

                '</div>' +


                '<div class="overview-box">' +

                '<div class="overview-bar red-bg" id="ovFailBar" style="height:' + ((failedTests / totalTests) * 220) + 'px;"></div>' +
                '<div class="overview-count red" id="ovFail">' + failedTests + '</div>' +
                '<div class="overview-label">Failed</div>' +

                '</div>' +

                '<div class="overview-box">' +

                '<div class="overview-bar yellow-bg" id="ovSkipBar" style="height:' + ((skippedTests / totalTests) * 220) + 'px;"></div>' +
                '<div class="overview-count yellow" id="ovSkip">' + skippedTests + '</div>' +
                '<div class="overview-label">Skipped</div>' +

                '</div>' +

                '</div>';

            // ================= FILTER =================

            setTimeout(function() {

                var filter =
                    document.getElementById('overviewFilter');

                if (!filter) return;

                filter.addEventListener('change', function() {

                    var val = this.value;

                    var pass = passedTests;
                    var fail = failedTests;
                    var skip = skippedTests;
                    var total = totalTests;

                    if (val !== 'ALL') {
                        total = moduleData[val].total;
                        pass = moduleData[val].pass;
                        fail = moduleData[val].fail;
                        skip = moduleData[val].skip;
                    }

                    document.getElementById('ovTotal').innerText = total;
                    document.getElementById('ovPass').innerText = pass;
                    document.getElementById('ovFail').innerText = fail;
                    document.getElementById('ovSkip').innerText = skip;


                    var maxTotal = total > 0 ? total : 1;

                    document.getElementById('ovTotalBar').style.height =
                        '220px';

                    document.getElementById('ovPassBar').style.height =
                        Math.max((pass / maxTotal) * 220, 8) + 'px';

                    document.getElementById('ovFailBar').style.height =
                        Math.max((fail / maxTotal) * 220, 8) + 'px';

                    document.getElementById('ovSkipBar').style.height =
                        Math.max((skip / maxTotal) * 220, 8) + 'px';

                });

            }, 200);

        }, 500);
    }

    function openDashboardByDefault() {
        var dashboardView = document.querySelector('.dashboard-view');

        if (!dashboardView)
            return;

        if (typeof window.toggleView === 'function') {
            window.toggleView('dashboard-view');
        } else {
            document.querySelectorAll('.view').forEach(function(view) {
                view.style.display = view.classList.contains('dashboard-view') ? '' : 'none';
            });

            document.querySelectorAll("[id^='nav-']").forEach(function(nav) {
                nav.classList.remove('active');
            });

            var dashboardNav = document.getElementById('nav-dashboard');
            if (dashboardNav)
                dashboardNav.classList.add('active');
        }
    }

    function moveDashboardNavToTop() {
        var dashboardNav = document.getElementById('nav-dashboard');
        var menu = document.querySelector('.side-nav-menu');

        if (!dashboardNav || !menu)
            return;

        var dashboardItem = dashboardNav.closest('li');
        if (dashboardItem && menu.firstElementChild !== dashboardItem)
            menu.insertBefore(dashboardItem, menu.firstElementChild);
    }

    function releaseDefaultViewLockOnUserClick() {
        var menu = document.querySelector('.side-nav-menu');

        if (!menu || menu.getAttribute('data-default-lock-listener') === 'true')
            return;

        menu.setAttribute('data-default-lock-listener', 'true');
        menu.addEventListener('click', function() {
            document.body.classList.add('ext-view-ready');
        }, true);
    }

    // ================= LOAD =================

    buildNavbar();

    createDashboard();

    moveDashboardNavToTop();

    releaseDefaultViewLockOnUserClick();

    openDashboardByDefault();

    document.addEventListener('DOMContentLoaded', function() {

        buildNavbar();

        createDashboard();

        moveDashboardNavToTop();

        releaseDefaultViewLockOnUserClick();

        openDashboardByDefault();

    });

    window.addEventListener('load', function() {

        setTimeout(buildNavbar, 300);

        setTimeout(createDashboard, 300);

        setTimeout(moveDashboardNavToTop, 300);

        setTimeout(releaseDefaultViewLockOnUserClick, 300);

        setTimeout(openDashboardByDefault, 350);

        setTimeout(openDashboardByDefault, 900);

    });

})();
