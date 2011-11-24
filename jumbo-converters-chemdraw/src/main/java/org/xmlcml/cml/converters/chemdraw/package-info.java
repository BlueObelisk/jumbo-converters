/** 
<h2>Converts Chemdraw CDX and CDXML files to CML.</h2>
Most of the semantic aspects of CDX are managed - some of the purely display elements are omitted.
The code is a 2-pass system:
<ul>
<li>CDX->CDXML</li>
<li>CDXML->CML</li>
</ul>
The cML is semi structured. It may be better to create a 
*/
package org.xmlcml.cml.converters.chemdraw;