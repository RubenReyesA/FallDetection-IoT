<template>
  <v-app class="grey lighten-4">
    <!-- Root -->
    
    <Navbar @refresh="refreshPage"></Navbar>
  <v-main>
      <!-- All content should be inside this -->
      <div class="container">
      <div class="row">
        <div class="col-12">
          <router-view/>
        </div>
      </div>
      </div>
  </v-main>
  
    
  </v-app>
</template>

<script>
import Navbar from "@/components/Navbar";

export default {
  name: "App",
  components: {
    Navbar,
  },
  methods:{
    refreshPage: function(){
      window.location.reload();
    },
  }
};
</script>

<style lang="scss">

@import "styles/style.css";

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

#nav {
  padding: 30px;

  a {
    font-weight: bold;
    color: #2c3e50;

    &.router-link-exact-active {
      color: #42b983;
    }
  }
}
</style>
