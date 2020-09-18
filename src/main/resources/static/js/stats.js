var statsChart = echarts.init(document.getElementById('stats'));
var statsOption = {
    title: {
        text: '增长',
        left:50
    },
    tooltip: {
        trigger: 'axis'
    },
    legend: {
        data:['新用户','总用户','订单','GMV']
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['周一','周二','周三','周四','周五','周六','周日']
    },
    yAxis: {
        type: 'value'
    },
    series: [
        {
            name:'新用户',
            type:'line',
            stack: '总量',
            data:[120, 132, 101, 134, 90, 230, 210]
        },
        {
            name:'总用户',
            type:'line',
            stack: '总量',
            data:[599, 731, 832, 966, 1056, 1286, 1496]
        },
        {
            name:'订单',
            type:'line',
            stack: '总量',
            data:[150, 201, 232, 354, 490, 530, 710]
        },
        {
            name:'GMV',
            type:'line',
            stack: '总量',
            data:[232.8, 354.6, 405.8, 634, 790.5, 830.2, 990.7]
        }
    ]
};

statsChart.setOption(statsOption);