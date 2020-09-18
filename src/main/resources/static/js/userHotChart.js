var userHotChart = echarts.init(document.getElementById('user-hot'));
var userHotOption = {
    title: {
        text: '热度',
        x: 'center'
    },
    tooltip: {
        trigger: 'item',
        formatter: "{a} <br/>{b} : {c} ({d}%)"
    },

    series: [
        {
            name: '热度属性',
            type: 'pie',
            radius: '55%',
            center: ['50%', '60%'],
            data: [
                {value: 335, name: '已注册'},
                {value: 310, name: '已下单'},
                {value: 234, name: '已领券'},
                {value: 234, name: '已复购'},
                {value: 135, name: '已完善'}
            ],
            itemStyle: {
                emphasis: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }
    ]
};

userHotChart.setOption(userHotOption);