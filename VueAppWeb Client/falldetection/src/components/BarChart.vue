<script>
import { Line } from "vue-chartjs";

export default {
  extends: Line,
  props: ["info", "update"],
  watch: {
    update: function () {
      if (this.update[this.update.length - 1] == true) {
        this.sync();
        this.renderChart(this.chartData, this.options);
      }
    },
  },
  data: function () {
    return {
      chartData: {
        labels: null,
        datasets: [
          {
            label: "Average temperature",
            borderWidth: 1,
            fill: false,
            borderColor: '#255d00',
            backgroundColor: '#558b2f',
            data: null,
          },
          {
            label: "Maximum temperature",
            borderWidth: 1,
            fill: false,
            borderColor: '#9a0007',
            backgroundColor: '#d32f2f',
            data: null,
          },
          {
            label: "Minimum temperature",
            borderWidth: 1,
            fill: false,
            borderColor: '#005cb2',
            backgroundColor: '#1e88e5',
            data: null,
          },
        ],
      },
      options: {
        scales: {
          yAxes: [
            {
              ticks: {
                beginAtZero: true,
              },
              gridLines: {
                display: true,
              },
            },
          ],
          xAxes: [
            {
              gridLines: {
                display: false,
              },
            },
          ],
        },
        legend: {
          display: true,
        },
        responsive: true,
        maintainAspectRatio: false,
      },
    };
  },
  methods: {
    sync: function () {
      this.chartData.labels = this.info.labels;
      this.chartData.datasets[0].data = this.info.freqArray1;
      this.chartData.datasets[1].data = this.info.freqArray2;
      this.chartData.datasets[2].data = this.info.freqArray3;
    },
  },
  mounted: function () {
    this.sync();
    this.renderChart(this.chartData, this.options);
  },
};
</script>