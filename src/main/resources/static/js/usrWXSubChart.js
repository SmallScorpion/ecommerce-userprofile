var userWXSubChart = echarts.init(document.getElementById('user-reg-channel'));
var userWXSubOption = {
    title : {
        text: '关注',
        x:'center'
    },
    tooltip : {
        trigger: 'item',
        formatter: "{a} <br/>{b} : {c} ({d}%)"
    },
    series : [
        {
            name: '关注微信公众号',
            type: 'pie',
            radius : '55%',
            center: ['50%', '60%'],
            data:[
                {value:335, name:'已关注'},
                {value:310, name:'未关注'}
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

userWXSubChart.setOption(userWXSubOption);