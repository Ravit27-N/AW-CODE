API_NG

## Git
#### Clear cache
Clear cache is when add some file to gitignore, but it does not ignore the files.
What you need to do is `Git Clear Cache`. 
TO SO: 
````shell
git rm -r --cached .
git add .
git commit -m 'git cache cleared'
git push
````