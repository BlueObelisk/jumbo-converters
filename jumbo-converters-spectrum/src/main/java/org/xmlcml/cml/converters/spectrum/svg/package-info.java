/** 
Extracts a CML spectrum from SVG graphics.
This is heuristic - it attempts to interpret SVG primitives (lines and text ) as
a 1D spectrum. Can be quite good in favourable cases,. Depends on the software used to
create the spectrum (e.g where the surrounding boxes, etc. are)
*/
package org.xmlcml.cml.converters.spectrum.svg;