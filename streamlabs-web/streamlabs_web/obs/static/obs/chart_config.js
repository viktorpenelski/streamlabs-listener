// requires an imported chartjs where used.
// e.g. <script src="https://cdn.jsdelivr.net/npm/chart.js@2.9.4/dist/Chart.min.js"></script>
//
// docs form bar chart: https://www.chartjs.org/docs/latest/charts/bar.html


function createChart(labels, data, context) {
    new Chart(context, {
        // The type of chart we want to create
        type: 'bar',

        // The data for our dataset
        data: {
            labels: labels,
            datasets: [{
                backgroundColor: 'rgb(255, 255, 224)',
                borderColor: 'rgb(97, 97, 132)',
                data: data
            }]
        },

        // Configuration options go here
        options: {
            legend: {
                display: false
            },
            animation: {
                duration: 1,
                onComplete: function () {
                    let chartInstance = this.chart;
                        ctx = chartInstance.ctx;

                        ctx.fillStyle = 'rgb(255, 255, 224)';
                        ctx.fillStyle = 'red';
                        ctx.font = Chart.helpers.fontString(
                            48,
                            Chart.defaults.global.defaultFontStyle,
                            Chart.defaults.global.defaultFontFamily
                        );
                        ctx.textAlign = 'center';
                        ctx.textBaseline = 'bottom';

                    this.data.datasets.forEach(function (dataset, i) {
                        let meta = chartInstance.controller.getDatasetMeta(i);
                        meta.data.forEach(function (bar, index) {
                            let data = dataset.data[index];
                            // make sure that numbers are roudned to 2 decimals
                            data = parseFloat(data).toFixed(2);
                            console.log(bar._model);
                            ctx.fillText(data, bar._model.x, bar._model.y - 5);
                        });
                    });
                }
            },
            scales: {
                ticks: {
                    backdropColor: 'black'
                },
                xAxes: [{
                    gridLines: {
                        display: false
                    },
                    ticks: {
                        fontColor: 'rgb(255, 255, 224)',
                        fontSize: 40,
                    }
                }],
                yAxes: [{
                    gridLines: {
                        display: false
                    },
                    ticks: {
                        padding: 25,
                        display: false,
                        beginAtZero: true,
                        // fontSize: 30,
                    }
                }]
            }
        }
    });
}

function drawChartFrom(response, chartContext) {
    let labels = []
    let amounts = []
    response.aggregated_tags.forEach((tag) => {
        labels.push(tag.tag)
        amounts.push(tag.amount)
    });

    createChart(labels, amounts, chartContext);
}