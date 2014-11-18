<div class="leaflet-top leaflet-left">
    <div class="leaflet-bar leaflet-control">
        <a data-toggle="modal" data-target="#helpModal">
            <i class="fa fa-lg fa-info-circle"></i>
        </a>
    </div>
</div>

<div class="modal fade" id="helpModal" tabindex="-1" role="dialog" aria-labelledby="helpModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span>
                    <span class="sr-only">Close</span>
                </button>
                <h4 class="modal-title" id="helpModalLabel">ABRAID-MP Data Validation</h4>
            </div>
            <div class="modal-body">
                <iframe src="<@spring.url "/help"/>"></iframe>
            </div>
            <div class="modal-footer"></div>
        </div>
    </div>
</div>
