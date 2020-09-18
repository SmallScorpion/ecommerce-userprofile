var newUserCouponChart = echarts.init(document.getElementById('new-user-coupon'));
var newUserCouponOption = {
    title : {
        text: '首杯免费提醒',
        x:'center'
    },
    tooltip : {
        trigger: 'item',
        formatter: "{a} <br/>{b} : {c} ({d}%)"
    },

    series : [
        {
            name: '消费属性',
            type: 'pie',
            radius : '55%',
            center: ['50%', '60%'],
            data:[
                {value:335, name:'前一天'},
                {value:310, name:'前两天'},
                {value:234, name:'前三天'},
                {value:234, name:'前四天'},
                {value:234, name:'前五天'},
                {value:234, name:'前六天'},
                {value:135, name:'前七天'}
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

newUserCouponChart.setOption(newUserCouponOption);