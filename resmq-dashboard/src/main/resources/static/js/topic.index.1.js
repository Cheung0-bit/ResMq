$(function() {

    const tableData = {};

    const dataTable = $("#data_list").dataTable({
        "deferRender": true,
        "processing" : true,
        "serverSide": true,
        "ajax": {
            url: base_url + "/topic/pageList",
            data : function ( d ) {
                console.log(d)
                var obj = {};
                obj.start = d.start;
                obj.length = d.length;
                obj.bizId = $('#bizId').val();
                obj.topic = $('#topic').val();
                return obj;
            }
        },
        "searching": false,
        "ordering": false,
        "columns": [
            {
                data: 'bizId',
                width: '20%',
                render : function ( data, type, row ) {
                    return bizListObj[ data+'' ]
                }
            },
            { data: 'topic', width: '40%'},
            { data: 'author', width: '20%'},
            { data: 'alarmEmails', visible: false},
            {
                data: 'opt' ,
                width: '20%',
                "render": function ( data, type, row ) {
                    return function(){

                        // data
                        tableData['key'+row.topic] = row;

                        var messageUrl = base_url + "/message?topic=" + row.topic;
                        var messageBtn = '<button class="btn btn-info btn-xs" type="button" onclick="javascript:window.open(\'' + messageUrl + '\')" >消息</button>  ';

                        // opt
                        var html = '<p topic="'+ row.topic +'" >'+
                            messageBtn +
                            '<button class="btn btn-warning btn-xs topic_update" type="button">编辑</button>  '+
                            '<button class="btn btn-danger btn-xs topic_remove" type="button">删除</button>  '+
                            '</p>';
                        return html;
                    };
                }
            }
        ],
        "language" : {
            "sProcessing" : "处理中...",
            "sLengthMenu" : "每页 _MENU_ 条记录",
            "sZeroRecords" : "没有匹配结果",
            "sInfo" : "第 _PAGE_ 页 ( 总共 _PAGES_ 页 ) 总记录数 _MAX_ ",
            "sInfoEmpty" : "无记录",
            "sInfoFiltered" : "(由 _MAX_ 项结果过滤)",
            "sInfoPostFix" : "",
            "sSearch" : "搜索:",
            "sUrl" : "",
            "sEmptyTable" : "表中数据为空",
            "sLoadingRecords" : "载入中...",
            "sInfoThousands" : ",",
            "oPaginate" : {
                "sFirst" : "首页",
                "sPrevious" : "上页",
                "sNext" : "下页",
                "sLast" : "末页"
            },
            "oAria" : {
                "sSortAscending" : ": 以升序排列此列",
                "sSortDescending" : ": 以降序排列此列"
            }
        }
    });

    // search btn
    $('#searchBtn').on('click', function(){
        dataTable.fnDraw();
    });


});
