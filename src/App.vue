<template>
  <div id="app">
<Index></Index>
  </div>
</template>

<script>

import IndexView from "./components/IndexView.vue";

export default {
  name: 'App',
  components: {
      Index: IndexView
  }
}
</script>

<style>
#app {
     height: 100%;
}
</style>
