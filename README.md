# Quicksort with Bufferpool

Implements a quicksort for binary files that are too large for memory
Message Passing approach

Implements
- Quicksort
  - Sorts based on the first 2 bytes of a record
    - Each record is 4 bytes
  - Communicates with buffer pool
  - Enforces insertion sort for small partitions
- Buffer pool
  - Minimizes disk access
  - Variable number of buffers
  - Organizes using Least Recently Used (LRU) Replacement

Notes : Stat doc to report the number of disk and buffer accesses
