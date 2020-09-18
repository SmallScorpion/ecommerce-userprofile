var userGenderChart = echarts.init(document.getElementById('user-gender'));
var userGenderOption = {
    title : {
        text: '性别',
        x:'center'
    },
    tooltip : {
        trigger: 'item',
        formatter: "{a} <br/>{b} : {c} ({d}%)"
    },

    series : [
        {
            name: '用户性别',
            type: 'pie',
            radius : '55%',
            center: ['50%', '60%'],
            data:[
                {value:335, name:'未知'},
                {value:310, name:'男性'},
                {value:234, name:'女性'}
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

userGenderChart.setOption(userGenderOption);