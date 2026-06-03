INSERT INTO Usuario( email, password, rol, activo) 
SELECT 'test@unlam.edu.ar', 'test', 'ADMIN', true
WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE email = 'test@unlam.edu.ar');

INSERT INTO Usuario(email, password, rol, activo) 
SELECT 'test2@unlam.edu.ar', 'test', 'USER', true
WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE email = 'test2@unlam.edu.ar');

INSERT INTO Usuario(email, password, rol, activo) 
SELECT 'test3@unlam.edu.ar', 'test', 'USER', true
WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE email = 'test3@unlam.edu.ar');

INSERT INTO Usuario(email, password, rol, activo) 
SELECT 'test4@unlam.edu.ar', 'test', 'USER', true
WHERE NOT EXISTS (SELECT 1 FROM Usuario WHERE email = 'test4@unlam.edu.ar');