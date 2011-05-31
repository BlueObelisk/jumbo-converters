
start ${jobInfo.id}
title "${jobInfo.title}"

geometry units an noautoz nocenter noautosym
<#list molecule.atoms as atom>
  ${atom.elementType}  ${atom.x3}  ${atom.y3}  ${atom.z3}
</#list>
end

basis
<#list methodBasis.atomBasisList as atomBasis>
  ${atomBasis.element} library ${atomBasis.basis}
</#list>
end

<#if methodBasis.method == "DFT">
dft
  xc ${methodBasis.functional}
end
</#if>

<#if calculationComponents.has("FUKUI")>
  set dft:condfukui 1
</#if>

<#if calculationComponents.has("DFT")>
task dft
</#if>

