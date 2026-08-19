<script setup lang="ts">
import Appbar from './components/Appbar.vue'

</script>

<template>
  
  <v-app>
    <Appbar />
    <v-main class="bg-grey-lighten-4">
      <router-view />
    </v-main>
  </v-app>
</template>

<style>
/* Estilos globais limpos */
html, body {
  margin: 0;
  padding: 0;
  font-family: Roboto, -apple-system, BlinkMacSystemFont, "Segoe UI", Oxygen, Ubuntu, Cantarell, "Helvetica Neue", sans-serif;
}
</style>
