<div id="latestOccurrencesPanel" class="leaflet-top leaflet-right" style="display: none" data-bind="visible: occurrences().length > 0, preventBubble: true">
    <div class="leaflet-bar leaflet-control" >
        <div data-bind="click: toggle" style="cursor: pointer;">
            <a><span data-bind="text: showOccurrences() ? 'Hide' : 'Show'"></span> recent occurrences</a>
        </div>

        <div data-bind="if: showOccurrences">
            <div><hr /></div>
            <div data-bind="foreach: occurrences">
                <div><i class="fa fa-map-marker"></i>&nbsp;<span data-bind="text: locationName"></span></div>
                <div><i class="fa fa-calendar"></i>&nbsp;<span data-bind="date: occurrenceDate"></span></div>
                <a class="link" data-bind="attr: { href: alert.url || '#' }" target="_blank">
                    <i class="fa fa-external-link"></i>&nbsp;<span data-bind="text: alert.feedName"></span>
                </a>
                <div data-bind="ifnot: ($index() + 1) === $parent.occurrences().length"><hr /></div>
            </div>
            <div data-bind="if: count() > 5">
                <div><hr /></div>
                <div style="text-align: center">Showing the most recent 5 of <span data-bind="text: count"></span> occurrences</div>
            </div>
        </div>
    </div>
</div>
