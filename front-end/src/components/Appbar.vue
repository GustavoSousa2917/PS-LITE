<template>
  <v-app-bar class="appbar border-b" elevation="0" color="white">
    <v-container class="d-flex align-center">
      <!-- A Logo clicável com estilo tipográfico -->
      <router-link to="/processos" class="logo-link text-decoration-none">
        <v-app-bar-title class="appbar-title text-primary">
          <span class="font-weight-black text-h1">PS</span><span class="font-weight-light text-h6 text-grey-darken-1">lite</span>
        </v-app-bar-title>
      </router-link>

      <v-spacer></v-spacer>
      <nav class="nav-links"></nav>
    </v-container>
  </v-app-bar>
</template>

<style scoped>
@import './Appbar.css';

.logo-link {
  cursor: pointer;
  transition: opacity 0.2s;
}
.logo-link:hover {
  opacity: 0.8;
}
</style>