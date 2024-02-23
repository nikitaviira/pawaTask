module.exports = {
  root: true,
  env: {
    browser: true,
    node: true,
    es6: true,
    'vue/setup-compiler-macros': true
  },
  extends: [
    'eslint:recommended',
    'plugin:vue/recommended',
    'plugin:cypress/recommended',
    '@vue/standard',
    '@vue/typescript'
  ],
  rules: {
    // general
    semi: 'off',
    'no-console': 'warn',
    'no-debugger': 'warn',
    'space-before-function-paren': [2, 'never'],
    indent: ['error', 2, { SwitchCase: 1 }],
    'array-bracket-spacing': ['error', 'never'],
    'block-spacing': 'error',
    'comma-dangle': ['error', 'only-multiline'],
    curly: 'error',
    'keyword-spacing': 'error',
    'no-mixed-spaces-and-tabs': 'error',
    'no-multi-spaces': 'error',
    'no-trailing-spaces': ['error', {
      skipBlankLines: true,
      ignoreComments: true
    }],
    'no-var': 'error',
    'object-curly-spacing': ['error', 'always'],
    quotes: ['error', 'single'],
    'func-call-spacing': 'off',
    'arrow-parens': ['warn', 'always'],

    // vue rules
    'vue/no-multiple-template-root': 'off',
    'vue/array-bracket-spacing': 'error',
    'vue/arrow-spacing': 'error',
    'vue/block-spacing': 'error',
    'vue/brace-style': 'error',
    'vue/camelcase': 'error',
    'vue/comma-dangle': 'error',
    'vue/component-name-in-template-casing': 'error',
    'vue/eqeqeq': ['error', 'smart'],
    'vue/key-spacing': 'error',
    'vue/match-component-file-name': 'error',
    'vue/object-curly-spacing': 'error',
    'vue/dot-location': ['error', 'property'],
    'vue/keyword-spacing': 'error',
    'vue/no-deprecated-scope-attribute': 'error',
    'vue/no-empty-pattern': 'error',
    'vue/v-slot-style': 'error',
    'vue/valid-v-slot': 'error',
    'vue/script-indent': ['error', 2, {
      baseIndent: 1,
      switchCase: 1
    }],
    'vue/html-indent': 'error',
    'vue/html-quotes': 'error',
    'vue/html-closing-bracket-spacing': 'off',
    'vue/multi-word-component-names': 'off',
    'vue/no-ref-as-operand': 'error',
    'vue/no-v-for-template-key': 'off',

    // typescript
    '@typescript-eslint/semi': ['error', 'always'],
    '@typescript-eslint/no-empty-function': 'error',
    '@typescript-eslint/no-empty-interface': 'error',
    '@typescript-eslint/no-unused-vars': 'error',
    '@typescript-eslint/indent': ['error', 2]
  },

  parserOptions: {
    parser: '@typescript-eslint/parser'
  },

  overrides: [
    {
      files: [
        '**/__tests__/*.{j,t}s?(x)',
        '**/tests/unit/**/*.spec.{j,t}s?(x)'
      ],
      env: {
        jest: true
      }
    },
    {
      files: ['*.vue'],
      rules: {
        indent: 'off',
        '@typescript-eslint/indent': 'off'
      }
    }
  ]
};
