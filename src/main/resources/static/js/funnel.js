var funnelChart = echarts.init(document.getElementById('funnel'));
funnelOption = {
    title: {
        text: '漏斗图',
    },
    tooltip: {
        trigger: 'item',
        formatter: "{b} : {c}%"
    },
    toolbox: {
        feature: {
            dataView: {readOnly: false},
            restore: {},
            saveAsImage: {}
        }
    },
    legend: {
        data: ['展现', '点击', '加购', '下单', '复购', "储值"]
    },
    calculable: true,
    series: [
        {
            name: '漏斗图',
            type: 'funnel',
            left: '10%',
            top: 60,
            bottom: 60,
            width: '80%',
            min: 0,
            max: 100,
            minSize: '0%',
            maxSize: '100%',
            sort: 'descending',
            gap: 2,
            label: {
                show: true,
                position: 'inside'
            },
            labelLine: {
                length: 10,
                lineStyle: {
                    width: 1,
                    type: 'solid'
                }
            },
            itemStyle: {
                borderColor: '#fff',
                borderWidth: 1
            },
            emphasis: {
                label: {
                    fontSize: 20
                }
            },
            data: [
                {value: 20, name: '储值'},
                {value: 40, name: '复购'},
                {value: 55, name: '下单'},
                {value: 70, name: '加购'},
                {value: 85, name: '点击'},
                {value: 100, name: '展现'}
            ]
        }
    ]
};

funnelChart.setOption(funnelOption);