var invalidCouponChart = echarts.init(document.getElementById('user-coupon-invalid'));
var invalidCouponOption = {
    title : {
        text: '优惠券失效提醒',
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
                {value:335, name:'三天后失效'},
                {value:310, name:'五天后失效'},
                {value:234, name:'七天后失效'},
                {value:234, name:'一天后失效'}

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

invalidCouponChart.setOption(invalidCouponOption);