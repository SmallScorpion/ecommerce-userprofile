var userOrderCountChart = echarts.init(document.getElementById('user-order-count'));
var userOrderCountOption = {
    title : {
        text: '周下单量',
        left:50
    },
    legend:{},
    tooltip: {},
    dataset: {
        source: [
            ['时间', '当期', '上期', ],
            ['近一天', 43.3, 85.8],
            ['近二天', 83.1, 73.4],
            ['近三天', 86.4, 65.2],
            ['近四天', 86.4, 65.2],
            ['近五天', 86.4, 65.2],
            ['近六天', 72.4, 53.9],
            ['近七天', 72.4, 53.9],
        ]
    },
    xAxis: {type: 'category'},
    yAxis: {},
    // Declare several bar series, each will be mapped
    // to a column of dataset.source by default.
    series: [
        {type: 'bar'},
        {type: 'bar'}
    ]
};

userOrderCountChart.setOption(userOrderCountOption);
