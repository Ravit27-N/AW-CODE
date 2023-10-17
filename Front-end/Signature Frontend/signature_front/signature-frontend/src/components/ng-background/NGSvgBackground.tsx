import Box from '@mui/material/Box';

export function NgSvgBackground({resource}: {resource: string | undefined}) {
  return (
    <Box
      sx={{
        height: '100vh',
        overflow: 'hidden',
        position: 'relative',
      }}>
      <img
        src={resource}
        style={{height: '100%', position: 'absolute'}}
        alt={'bg'}
      />
    </Box>
  );
}
