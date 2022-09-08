program { int i int j
   i = 1
   j = 2
   switch (testSure) {
          case [testMore] # return 12
          case [test1, test2] # return 1
          case [test3, test4] # return 0
          case [ test ] # return 3
          default # return 5
   }
   i = 0
}