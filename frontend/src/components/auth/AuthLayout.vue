<template>
  <div class="container">
    <div class="form-container">
      <p class="brand">
        pawaTask
      </p>
      <slot />
    </div>
  </div>
</template>

<style scoped lang="scss">
  @import "@/assets/variables";

  .container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100%;

    .form-container {
      padding: 40px;
      background: white;
      min-width: 500px;
      border-radius: 20px;
      box-shadow: $shadow;
      display: flex;
      flex-direction: column;

      @media screen and (max-width: 600px) {
        min-width: 100%;
        height: 100%;
        padding: 30px;
      }

      .brand {
        font-weight: bold;
        font-size: 35px;
        margin-top: 0;
        margin-bottom: 15px;
        color: $main-color;
      }
    }
  }
</style>
