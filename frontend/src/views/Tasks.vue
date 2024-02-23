<template>
  <div class="task-container">
    <div style="display: flex; justify-content: space-between;">
      <p class="brand">
        pawaTask
      </p>
      <button class="add-task">
        Add a new task
      </button>
    </div>
    <hr>
    <span>You do not have any tasks.</span> <span class="add-task-link">Add a new task</span>
  </div>
</template>

<script setup lang="ts">

</script>

<style lang="scss" scoped>
  @import "@/assets/variables";

  .brand {
    font-weight: bold;
    font-size: 25px;
    margin: 0;
    color: $main-color;
  }

  .task-container {
    position: fixed;
    top: 20px;
    left: 50%;
    transform: translateX(-50%);
    width: 640px;
    height: 800px;
    padding: 20px;
    background: white;
    box-shadow: $shadow;

    @media screen and (max-width: 700px) {
      height: 100%;
      box-shadow: none;
      width: 100%;
      top: 0;
    }
  }

  .add-task {
    background: $main-color;
    color: white;
    padding: 5px 15px;
    font-size: 14px;
    cursor: pointer;
    font-weight: bold;
  }

  .add-task-link {
    color: $main-color;
    text-decoration: underline;
    font-weight: bold;
    cursor: pointer;
  }

  hr {
    display: block;
    height: 1px;
    border: 0;
    border-top: 1px solid #ccc;
    margin: 1em 0;
    padding: 0;
  }

  span {
    font-size: 14px;
  }
</style>
