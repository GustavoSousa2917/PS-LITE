<script setup lang="ts">
import appbar from './components/Appbar.vue'
</script>

<template>
  <header class ="appbar">
    <appbar />
  </header>
    <div class="content">
      <router-view />
      </div>
</template>

<style scoped>
:global(body) {
  margin: 0;
  font-family: Arial, sans-serif;
  background: #f3f4f6;
}
</style>
