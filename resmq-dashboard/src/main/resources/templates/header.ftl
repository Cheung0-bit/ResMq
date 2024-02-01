<header class="fixed-top" id="header">
    <div class="container d-flex">
        <div class="logo mr-auto">
            <h1 class="text-danger"><a href="${urlPrefix}">ResMq</a></h1>
        </div>
        <nav class="nav-menu display-none d-lg-block">
            <ul>
                <li class=${topicsActive!""}>
                    <a href="${urlPrefix}/topics">
                        <span>Topics</span>
                    </a>
                </li>
                <li class=${scheduledActive!""}>
                    <a href="${urlPrefix}/scheduled">
                        <span>Scheduled</span>
                    </a>
                </li>
                <li class=${pendingActive!""}>
                    <a href="${urlPrefix}/pending">
                        <span>Pending</span>
                    </a>
                </li>
                <li class=${deadActive!""}>
                    <a href="${urlPrefix}/dead">
                        <span>Dead</span>
                    </a>
                </li>
            </ul>
        </nav>
    </div>
</header>