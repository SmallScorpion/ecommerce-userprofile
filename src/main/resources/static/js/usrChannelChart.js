var userChannelChart = echarts.init(document.getElementById('user-channel'));
var userChannelOption = {
    title : {
        text: '渠道',
        x:'center'
    },
    tooltip : {
        trigger: 'item',
        formatter: "{a} <br/>{b} : {c} ({d}%)"
    },

    series : [
        {
            name: '接入渠道',
            type: 'pie',
            radius : '55%',
            center: ['50%', '60%'],
            data:[
                {value:335, name:'IOS'},
                {value:310, name:'Android'},
                {value:234, name:'微信小程序'},
                {value:135, name:'微信公众号'},
                {value:1548, name:'H5'}
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

userChannelChart.setOption(userChannelOption);