<template>
  <div id="app">
    <IndexView></IndexView>
  </div>
</template>

<script>

import IndexView from "./components/IndexView.vue";

export default {
  name: 'App',
  components: {
      IndexView: IndexView
  }
}
</script>

<style>
#app {
     height: 100%;
}
</style>
