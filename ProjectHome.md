Using LWJGL's OpenCL binding this program renders the buddahbrot, a variation of the mandelbrot fractal.

This problem is not suited for the GPU at all (so many increments of memory at "random" positions) and the code is rather inefficient and naïve. However x4-x20 performance is achieved.

It also contains several techniques for representing a false color density maps.