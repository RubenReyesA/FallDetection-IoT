<template>
  <div class="home">
    <div class="updateText">
      <h5>{{time}}</h5>
      <v-btn
        color="secondary"
        fab
        x-small
        dark
        class="btn"
        :disabled="dis"
        @click="callFirebase"
      >
        <v-icon>mdi-update</v-icon>
      </v-btn>
    </div>
    <div>
      <h3 class="title">Teruel (Calamocha) Weekly Temperature (2020)</h3>
      <bar-chart :info="data" :update="update" />
    </div>

  </div>
</template>

<script>

import BarChart from "@/components/BarChart";

import fb from "@/fb";

export default {
  name: "Home",
  components: {
    BarChart,
  },
  data: function () {
    return {
      time: null,
      tabs: null,
      update: [],
      dis: false,
      data: {
        size: null,
        labels: [],
        freqArray1: [],
        freqArray2: [],
        freqArray3: [],
      },
    };
  },
  methods: {
    updateCharts: function () {
      this.dis = true;

      var da = Date.now();
      var d = new Date(da);
      var months = [
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December",
      ];
      let time =
        d.getDate() +
        " " +
        months[d.getMonth()] +
        " " +
        d.getFullYear() +
        " - " +
        d.getHours() +
        ":" +
        (d.getMinutes()<10?'0':'') + d.getMinutes() +
        ":" +
        (d.getSeconds()<10?'0':'') + d.getSeconds();

        this.time = "Updated on: " + time;
    },
    callFirebase: function () {
      var rawData = [];
      fb.ref("/").on("value", (snapshot) => {
        snapshot.forEach(function (childSnapshot) {
          rawData.push(childSnapshot.val());
        });
        
        this.updateCharts();
        this.data.labels = [];
        this.data.freqArray = [];
        

        for (let i = 1; i <= rawData.length; i++) {
          this.data.labels.push(i); // CALCULATE LABELS
        }

        // CALCULATE FREQUENCIES ARRAY
        for (let i = 0; i < rawData.length; i++) {
          this.data.freqArray1[i] = rawData[i]["med"];
          this.data.freqArray2[i] = rawData[i]["max"];
          this.data.freqArray3[i] = rawData[i]["min"];
        }

        this.data.size = this.data.labels.length; //CALCULATE SIZE

        this.update.push(true);
        this.dis = false;
        rawData = [];
      });
    },
  },
  created: function () {
    this.callFirebase();
  },
};
</script>
