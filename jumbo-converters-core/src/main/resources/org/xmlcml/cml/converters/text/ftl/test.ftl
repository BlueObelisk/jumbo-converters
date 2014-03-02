<html>
<head>
  <title>Welcome!</title>
</head>
<body>
  <h1>Welcome ${user}!</h1>
  <p>Our latest product:
  <a href="${latestProduct.url}">${latestProduct.name}</a>!
  
  ID=${molecule.id}
  <#list molecule.atoms as atom>
ATOM ${atom.elementType}  ${atom.x3}  ${atom.y3}  ${atom.z3}
</#list>
  
</body>
</html>  